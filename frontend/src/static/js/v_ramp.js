const rampSize = 0.01;


function getLink(user, slice, props) {
  if (props.mergegroup) {
    return `${window.getBaseLink(slice.repoId)}/commit/${slice.hash}`;
  }

  return `${window.getBaseLink(user.repoId)}/commit/${slice.hash}`;
}

function getWidth(slice, props) {
  if (slice.insertions === 0) {
    return 0;
  }

  const newSize = 100 * (slice.insertions / props.avgsize);
  return Math.max(newSize * rampSize, 0.5);
}

function getContributionMessage(slice, commit, props) {
  let title = '';
  if (props.tframe === 'commit') {
    return `[${slice.date}] ${commit.messageTitle}: ${commit.insertions} lines`;
  }

  title = props.tframe === 'day'
        ? `[${slice.date}] Daily `
        : `[${slice.date} till ${slice.endDate}] Weekly `;
  title += `contribution: ${slice.insertions} lines`;
  return title;
}

function openTabZoom(user, slice, props, store) {
  return function(evt) {
    // prevent opening of zoom tab when cmd/ctrl click
    if (window.isMacintosh ? evt.metaKey : evt.ctrlKey) {
      return;
    }

    const zoomUser = { ...user };
    zoomUser.commits = user.dailyCommits;

    const info = {
      zRepo: user.repoName,
      zAuthor: user.name,
      zFilterGroup: props.groupby,
      zTimeFrame: 'commit',
      zAvgCommitSize: slice.insertions,
      zUser: zoomUser,
      zLocation: window.getBaseLink(user.repoId),
      zSince: slice.date,
      zUntil: props.tframe === 'day' ? slice.date : slice.endDate,
      zIsMerge: props.mergegroup,
      zFromRamp: true,
      zFilterSearch: props.filtersearch,
    };
    window.deactivateAllOverlays();

    store.commit('updateTabZoomInfo', info);
  }
}

// position for commit granularity
function getCommitPos(i, total, props) {
  return (total - i - 1) * window.DAY_IN_MS / total
      / (this.getTotalForPos(props.sdate, props.udate) + window.DAY_IN_MS);
}

// position for day granularity
function getSlicePos(date, props) {
  const total = getTotalForPos(props.sdate, props.udate);
  return (new Date(props.udate) - new Date(date)) / (total + window.DAY_IN_MS);
}

// get duration in miliseconds between 2 date
function getTotalForPos(sinceDate, untilDate) {
  return new Date(untilDate) - new Date(sinceDate);
}

function getSliceColor(date, props) {
  const timeMs = props.fromramp
      ? (new Date(props.sdate)).getTime()
      : (new Date(date)).getTime();

  return (timeMs / window.DAY_IN_MS) % 5;
}

// Prevent browser from switching to new tab when clicking ramp
function rampClick(evt) {
  const isKeyPressed = window.isMacintosh ? evt.metaKey : evt.ctrlKey;
  if (isKeyPressed) {
    evt.preventDefault();
  }
}

window.vRamp = {
  functional: true,
  props: ['groupby', 'user', 'tframe', 'avgsize', 'sdate', 'udate', 'mergegroup', 'fromramp', 'filtersearch'],

  render(createElement, { props }) {
    const user = props.user;
    const slices = []
    if (props.tframe === 'commit') {
      user.commits.forEach((slice, j) => {
        slice.commitResults.forEach((commit, k) =>  {
          if (commit.insertions>0) {
            const ramp = createElement(
              'a',   // tag name
              {
                class: ['ramp__slice ramp__slice--color' + getSliceColor(slice.date, props)],
                style: {
                  "zIndex": user.commits.length - j,
                  "borderLeftWidth": getWidth(commit, props) + 'em',
                  "right": ((getSlicePos(slice.date, props)
                    + (getCommitPos(k, slice.commitResults.length, props))) * 100) + '%'
                },
                // Normal HTML attributes
                attrs: {
                  draggable: "false",
                  target: "_blank",
                  title: getContributionMessage(slice, commit, props),
                  href: getLink(user, commit, props),
                },
                // Event handlers are nested under `on`
                on: {
                  click: rampClick
                },
              }
            )
            slices.push(ramp)
          }
        });
      });
    } else {
      user.commits.forEach((slice, j) => {
        if (slice.insertions>0) {
          const ramp = createElement(
            'a',   // tag name
            {
              class: ['ramp__slice ramp__slice--color' + getSliceColor(slice.date, props)],
              style: {
                "zIndex": user.commits.length - j,
                "borderLeftWidth": getWidth(commit, props) + 'em',
                "right": (getSlicePos(props.tframe === 'day' ? slice.date : slice.endDate, props) * 100) + '%'
              },
              // Normal HTML attributes
              attrs: {
                draggable: "false",
                title: getContributionMessage(slice, false, props),
              },
              // Event handlers are nested under `on`
              on: {
                click: openTabZoom(user, slice, props, this.$store)
              },
            }
          )
          slices.push(ramp)
        }
      })
    }
    return createElement(
      'div',
      {
        attrs: { class: "ramp" },
      },
      slices
    )
  },

};
