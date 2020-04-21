/* eslint-disable no-undef */

Vue.use(VueStrap);

function scrollToUrlAnchorHeading() {
  if (window.location.hash) {
    // remove leading hash to get element ID
    const headingElement = document.getElementById(window.location.hash.slice(1));
    if (headingElement) {
      headingElement.scrollIntoView();
      window.scrollBy(0, -document.body.style.paddingTop.replace('px', ''));
    }
  }
}

function insertCss(cssCode) {
  const newNode = document.createElement('style');
  newNode.innerHTML = cssCode;
  document.getElementsByTagName('head')[0].appendChild(newNode);
}

function setupAnchorsForFixedNavbar() {
  const headerSelector = jQuery('header');
  const isFixed = headerSelector.filter('.header-fixed').length !== 0;
  if (!isFixed) {
    return;
  }

  const headerHeight = headerSelector.height();
  const bufferHeight = 1;
  jQuery('.nav-inner').css('padding-top', `calc(${headerHeight}px)`);
  jQuery('#content-wrapper').css('padding-top', `calc(${headerHeight}px)`);
  insertCss(
    `span.anchor {
    display: block;
    position: relative;
    top: calc(-${headerHeight}px - ${bufferHeight}rem)
    }`,
  );
  jQuery('h1, h2, h3, h4, h5, h6, .header-wrapper').each((index, heading) => {
    if (heading.id) {
      /**
       * Fixing the top navbar would break anchor navigation,
       * by creating empty spans above the <h> tag we can prevent
       * the headings from being covered by the navbar.
       */
      const spanId = heading.id;
      heading.insertAdjacentHTML('beforebegin', `<span id="${spanId}" class="anchor"></span>`);
      jQuery(heading).removeAttr('id'); // to avoid duplicated id problem
    }
  });
}

function updateSearchData(vm) {
  jQuery.getJSON(`${baseUrl}/siteData.json`)
    .then((siteData) => {
      vm.searchData = siteData.pages;
    });
}

const MarkBind = {
  executeAfterSetupScripts: jQuery.Deferred(),
};

MarkBind.afterSetup = (func) => {
  if (document.readyState !== 'loading') {
    func();
  } else {
    MarkBind.executeAfterSetupScripts.then(func);
  }
};

function removeTemporaryStyles() {
  jQuery('.temp-navbar').removeClass('temp-navbar');
  jQuery('.temp-dropdown').removeClass('temp-dropdown');
  jQuery('.temp-dropdown-placeholder').remove();
}

function executeAfterCreatedRoutines() {
  removeTemporaryStyles();
}

function executeAfterMountedRoutines() {
  scrollToUrlAnchorHeading();
  setupAnchorsForFixedNavbar();
  MarkBind.executeAfterSetupScripts.resolve();
}

function setupSiteNav() {
  // Add event listener for site-nav-btn to toggle itself and site navigation elements.
  const siteNavBtn = document.getElementById('site-nav-btn');
  if (siteNavBtn) {
    siteNavBtn.addEventListener('click', function () {
      this.classList.toggle('shift');
      document.getElementById('site-nav').classList.toggle('open');
      document.getElementById('site-nav-btn-wrap').classList.toggle('open');
    });
  }
  // Creates event listener for all dropdown-btns in page.
  Array.prototype.forEach.call(
    document.getElementsByClassName('dropdown-btn'),
    dropdownBtn => dropdownBtn.addEventListener('click', function () {
      this.classList.toggle('dropdown-btn-open');
      const dropdownContent = this.nextElementSibling;
      const dropdownIcon = this.lastElementChild;
      dropdownContent.classList.toggle('dropdown-container-open');
      dropdownIcon.classList.toggle('rotate-icon');
    }),
  );
}

function setup() {
  // eslint-disable-next-line no-unused-vars
  const vm = new Vue({
    el: '#app',
    created() {
      executeAfterCreatedRoutines();
    },
    mounted() {
      executeAfterMountedRoutines();
    },
  });
  setupSiteNav();
}

function setupWithSearch() {
  const { searchbar } = VueStrap.components;
  // eslint-disable-next-line no-unused-vars
  const vm = new Vue({
    el: '#app',
    components: {
      searchbar,
    },
    data() {
      return {
        searchData: [],
      };
    },
    methods: {
      searchCallback(match) {
        const page = `${baseUrl}/${match.src.replace(/.(md|mbd)$/, '.html')}`;
        const anchor = match.heading ? `#${match.heading.id}` : '';
        window.location = `${page}${anchor}`;
      },
    },
    created() {
      executeAfterCreatedRoutines();
    },
    mounted() {
      executeAfterMountedRoutines();
      updateSearchData(this);
    },
  });
  setupSiteNav();
}

function makeInnerGetterFor(attribute) {
  return (element) => {
    const innerElement = element.querySelector(`[data-mb-html-for="${attribute}"]`);
    return innerElement === null ? '' : innerElement.innerHTML;
  };
}

function makeHtmlGetterFor(componentType, attribute) {
  return (element) => {
    const contentWrapper = document.getElementById(element.attributes.for.value);
    return contentWrapper.dataset.mbComponentType === componentType
      ? makeInnerGetterFor(attribute)(contentWrapper) : '';
  };
}

/* eslint-disable no-unused-vars */
/*
 These getters are used by triggers to get their popover/tooltip content.
 We need to create a completely new popover/tooltip for each trigger due to bootstrap-vue's implementation,
 so this is how we retrieve our contents.
*/
const popoverContentGetter = makeHtmlGetterFor('popover', 'content');
const popoverHeaderGetter = makeHtmlGetterFor('popover', 'header');
const popoverInnerContentGetter = makeInnerGetterFor('content');
const popoverInnerHeaderGetter = makeInnerGetterFor('header');

const popoverGenerator = { title: popoverHeaderGetter, content: popoverContentGetter };
const popoverInnerGenerator = { title: popoverInnerHeaderGetter, content: popoverInnerContentGetter };

const tooltipContentGetter = makeHtmlGetterFor('tooltip', '_content');
const tooltipInnerContentGetter = makeInnerGetterFor('_content');
/* eslint-enable no-unused-vars */

if (enableSearch) {
  setupWithSearch();
} else {
  setup();
}
