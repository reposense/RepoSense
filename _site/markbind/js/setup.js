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

function flattenModals() {
  jQuery('.modal').each((index, modal) => {
    jQuery(modal).detach().appendTo(jQuery('#app'));
  });
}

function insertCss(cssCode) {
  const newNode = document.createElement('style');
  newNode.innerHTML = cssCode;
  document.getElementsByTagName('head')[0].appendChild(newNode);
}

function setupAnchors() {
  const headerSelector = jQuery('header');
  const isFixed = headerSelector.filter('.header-fixed').length !== 0;
  const headerHeight = headerSelector.height();
  const bufferHeight = 1;
  if (isFixed) {
    jQuery('.nav-inner').css('padding-top', `calc(${headerHeight}px)`);
    jQuery('#content-wrapper').css('padding-top', `calc(${headerHeight}px)`);
    insertCss(
      `span.anchor {
      display: block;
      position: relative;
      top: calc(-${headerHeight}px - ${bufferHeight}rem)
      }`,
    );
  }
  jQuery('h1, h2, h3, h4, h5, h6, .header-wrapper').each((index, heading) => {
    if (heading.id) {
      jQuery(heading).on('mouseenter',
                         () => jQuery(heading).find('.fa.fa-anchor').css('visibility', 'visible'));
      jQuery(heading).on('mouseleave',
                         () => jQuery(heading).find('.fa.fa-anchor').css('visibility', 'hidden'));
      if (isFixed) {
        /**
         * Fixing the top navbar would break anchor navigation,
         * by creating empty spans above the <h> tag we can prevent
         * the headings from being covered by the navbar.
         */
        const spanId = heading.id;
        heading.insertAdjacentHTML('beforebegin', `<span id="${spanId}" class="anchor"></span>`);
        jQuery(heading).removeAttr('id'); // to avoid duplicated id problem
      }
    }
  });
  jQuery('.fa-anchor').each((index, anchor) => {
    jQuery(anchor).on('click', function () {
      window.location.href = jQuery(this).attr('href');
    });
  });
}

function updateSearchData(vm) {
  jQuery.getJSON(`${baseUrl}/siteData.json`)
    .then((siteData) => {
      // eslint-disable-next-line no-param-reassign
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
  flattenModals();
  scrollToUrlAnchorHeading();
  setupAnchors();
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

if (enableSearch) {
  setupWithSearch();
} else {
  setup();
}
