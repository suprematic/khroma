# Khroma changelog

## 0.2.0

- `commands`'s  namespace was mistakenly `debugger`
- `identity`'s namespace was mistakenly `extension`
- New `tabs/remove`

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