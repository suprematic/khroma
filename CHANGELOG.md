# Khroma changelog

## 0.2.0

### Breaking changes

- `tabs/get-tab` is now called `tabs/get`
- `tabs/get-active-tab` is now called `tabs/get-active`
- `tabs/get` and `tabs/get-active` were sending the value enveloped in a map. They now send the raw tab object to remain closer to the API. [See issue #12](https://github.com/suprematic/khroma/issues/12).
- `util/with-callback` now keywordizes the return value to provide consistent behavior with other functions. This is a potentially breaking change, and can affect you if you're using the `debugger` or `identity` namespaces.


### Other changes and improvements

- Khroma now includes externs for the Chrome extensions API, so that we can support `:advanced` compilation ([obtained from here](http://closureplease.com/externs/)).
- New `alarms` namespace (complete).
- New `context-menus` namespace (complete).
- New `web-navigation` namespace (WIP).
- New `tabs/move`, `tabs/query`, `tabs/remove`
- New event handler for `commands/on-command`
- New event handler for `tabs/on-activated`
- New `windows/get-all`, `windows/get-last-focused`
- New event handlers for `windows/on-created`, `windows/on-removed`, `windows/on-focus-changed`
- Surfaced all `runtime` events.
- `commands`'s  namespace was mistakenly `debugger`
- `identity`'s namespace was mistakenly `extension`
- `extension/get-views` now expects a Clojure map as a parameter, no need to to `clj->js` before calling it.


## 0.1.0

Includes potentially breaking changes.

- Renamed tabs and runtime event handlers for consistency, added deprecated wrapper for the old function names.  
- storage/get will now keywordize the keys on the dictionary being returned.
- Added extra parameters for storage functions to be able to indicate the area.
- BREAKING CHANGE: Default area for storage functions will be `local`.
- BREAKING CHANGE: Renamed the `browser` namespace to `browser-action`
- New tabs/update, tabs/activate functions
- New idle namespace


## 0.0.4

Hotfix for a khroma/util.cljs bug.

## 0.0.3

- New storage namespace. Wraps storage functions and events.
- New browser namespace. Currently has only an on-clicked event for receiving click notifications on the extension button. Will be used to wrap browserAction functions.
- New windows namespace. Will be used to wrap windows functions.
- Miscellaneous refactoring and documentation improvements.