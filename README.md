# Khroma

Khroma is a ClojureScript library which provides access to Chrome Extension API. It wraps API with idiomatic functions 
and heavily uses core.async for asynchronous API interaction.

Khroma is under active development, but not all APIs are implemented yet. The long term goal is to cover all Chrome 
and Chrome OS API's to make use of ClojureScript for extensions and applications development as convenient as possible.


## Upgrading

Khroma 0.3.0 contains breaking changes from 0.1.0. Make sure you read the change log.


## Installation

Add the following dependency to your `project.clj` file:

```clojure
[khroma "0.3.0"]
```

## Methodology

This repository will be maintained using [git-flow](http://nvie.com/posts/a-successful-git-branching-model/).

`develop` and `master` branches can be considered set in stone, but I'll be developing this publicly - if you see a feature branch, assume it's mercurial and may be amended or rebased in the future.
 
### Contributing

Pull requests are welcome, please make them from your clone's `develop` branch.

### Low-hanging fruit
 
- Finish exposing the events on web-navigation
- Event filtering examples
- `on-command` event and examples
- Finish exposing `tabs` and `windows` functions
  

## Tests and examples

See [khroma-tests.md](khroma-tests.md)


## Trivial example

```clojure
(ns example
  (:require [khroma.runtime :as runtime))
  
  
; receive messages for extension  
(let [messages (runtime/on-message)]
  (go-loop [] 
    (when-let [message (<! messages)]
      (process-message message)
      (recur)))
      
; send message to another extension      
(runtime/send-message 
   {:message-data "xx" :something-else "yyy"}
   {:extensionId "some.other.extension"
    :responseCallback 
      (fn [response]
        (handle-response response))})
```


## License

Copyright © 2015 [SUPREMATIC](http://www.suprematic.net).

Includes `khroma-tests` © 2015 [Numergent Limited](http://numergent.com/).

Distributed under the Eclipse Public License, the same as Clojure.
