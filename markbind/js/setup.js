/* eslint-disable no-undef */

Vue.use(MarkBindVue);

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
  const headerSelector = jQuery('header[fixed]');
  const isFixed = headerSelector.length !== 0;
  if (!isFixed) {
    return;
  }

  const headerHeight = headerSelector.height();
  const bufferHeight = 1;
  jQuery('.nav-inner').css('padding-top', `calc(${headerHeight}px + 1rem)`);
  jQuery('#content-wrapper').css('padding-top', `calc(${headerHeight}px)`);
  insertCss(
    `span.anchor {
    position: relative;
    top: calc(-${headerHeight}px - ${bufferHeight}rem)
    }`,
  );
  insertCss(`span.card-container::before {
        display: block;
        content: '';
        margin-top: calc(-${headerHeight}px - ${bufferHeight}rem);
        height: calc(${headerHeight}px + ${bufferHeight}rem);
      }`);
  jQuery('h1, h2, h3, h4, h5, h6, .header-wrapper').each((index, heading) => {
    if (heading.id) {
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

// eslint-disable-next-line no-unused-vars
function handleSiteNavClick(elem, useAnchor = true) {
  if (useAnchor) {
    const anchorElements = elem.getElementsByTagName('a');
    if (anchorElements.length) {
      window.location.href = anchorElements[0].href;
      return;
    }
  }
  const dropdownContent = elem.nextElementSibling;
  const dropdownIcon = elem.lastElementChild;
  dropdownContent.classList.toggle('site-nav-dropdown-container-open');
  dropdownIcon.classList.toggle('site-nav-rotate-icon');
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
}

function setupWithSearch() {
  const { searchbar } = MarkBindVue.components;
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
