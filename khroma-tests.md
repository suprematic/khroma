# khroma-tests

Tests and demo code for the [Khroma](https://github.com/suprematic/khroma) ClojureScript library.

## Rationale

Khroma cannot be tested by itself, since a lot of the APIs it uses are only exposed by Chrome for extensions with the proper permissions. Therefore, we need a test application which runs inside Chrome.

Once you have built and run the extension, it'll provide you with series of [devcards](https://github.com/bhauman/devcards) combining usage examples and live tests.

You can read more about it [here](http://numergent.com/2015-10/Using-devcards-for-testing-a-ClojureScript-Chrome-extension.html) or check out all other [khroma-related articles](http://numergent.com/tags/khroma/).


## Running

* `lein with-profile khroma-tests cljsbuild once`
* Go into your Chrome extensions and configure them for developer mode.
* Add the just built extension from the `extension` folder to Chrome.
* Press the shiny new lambda button on your browser bar.


## Cleaning up

The entire test suite is built to a single `khroma_tests.js` file, which is already being taken care of by `lein clean`. Make sure you retain the `extension` folder, as it contains necessary scaffolding for the Chrome extension (including the manifest).

## License

Copyright (c) 2015 [Numergent Limited](http://numergent.com/).  Distributed under the Eclipse Public License.