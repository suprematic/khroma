# Khroma changelog

## 0.1.0

- Renamed tabs and runtime event handlers for consistency, added deprecated wrapper for the old function names.  

## 0.0.4

Hotfix for a khroma/util.cljs bug.

## 0.0.3

- New storage namespace. Wraps storage functions and events, currently all invocations go to sync (need to decide if we will provide separate functions for local/sync).
- New browser namespace. Currently has only an on-clicked event for receiving click notifications on the extension button. Will be used to wrap browserAction functions.
- New windows namespace. Will be used to wrap windows functions.
- Miscellaneous refactoring and documentation improvements.