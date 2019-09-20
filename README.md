# web-specs [![Clojars Project](http://clojars.org/worldsingles/web-specs/latest-version.svg)](http://clojars.org/worldsingles/web-specs) [![cljdoc badge](https://cljdoc.org/badge/worldsingles/web-specs)](https://cljdoc.org/d/worldsingles/web-specs/CURRENT)

Low-level Specs for web APIs/form fields that coerce string->type and also generate strings.

## Usage

`deps.edn`:

``` clojure
clj -Sdeps '{:deps {worldsingles/web-specs {:mvn/version "0.1.0"}}}'
```

Leiningen / Boot Dependency:

``` clojure
[worldsingles/web-specs "0.1.0"]
```

All the specs defined here accept strings that coerce to a given type, as well as values of that type, and will generate strings that can be coerced to that type.

* `:ws.web.spec/boolean` -- a spec for `Boolean` values
* `:ws.web.spec/long` -- a spec for `long` values
* `:ws.web.spec/opt-long` -- a spec for "optional" `long` values (`nil` and `""` are accepted, `nil` will be generated -- but not `""`)
* `:ws.web.spec/pos-int` -- a spec for positive integer values (based on `pos-int?`)
* `:ws.web.spec/age` -- a spec for positive integer values in the range 18..120
* `:ws.web.spec/double` -- a spec for `double` values
* `:ws.web.spec/opt-double` -- a spec for "optional" `double` values (`nil` and `""` are accepted, `nil` will be generated -- but not `""`)
* `:ws.web.spec/date` -- a spec for date values
* `:ws.web.spec/opt-date` -- a spec for "optional" date values (`nil` and `""` are accepted, `nil` will be generated -- but not `""`)

For dates, the following formats are accepted:
* `yyyy/M/d`
* `M/d/yyyy`
* `yyyy-M-d`
* `M-d-yyyy`
* `M/d/yy` -- short years are considered 20xx
* `M-d-yy` -- short years are considered 20xx
* `EEE MMM dd HH:mm:ss zzz yyyy` -- e.g., `"Fri Sep 20 13:02:00 PDT 2019"`

All generated dates are strings of the form `MM/dd/yyyy`.

These specs are all built on two macros that wrap low-level coercions and predicates to produce string->type coercing specs:
* `param-spec`, `opt-param-spec`

These accept a "coercion function", an optional "stringify" function, and a spec:
* Coercion: accept a value of the target type, or a string representation of such a value and either convert it to the target type or produce `:clojure.spec.alpha/invalid`
* Stringify: accept a value of the target type and produce a string representation of it (that can be coerced back to the original value) -- this defaults to `str`
* Spec: a spec for the target type that will successfully generate values of that type

In addition, the following "coercions" are defined that accept strings or a given type, and produce `:clojure.spec.alpha/invalid` on bad input:
* `->boolean`, `->long`, `->double`, and `->date`

For `->long`, `->double`, and `->date` there are utility `coerce->...` functions that accept the given type or a string that they will attempt to coerce to the given type, throwing exceptions on bad input. These are used to build the `->...` coercions but are left public in case they are useful in other contexts.

Finally, there is a convenience function that accepts a comma-separated list of numbers and will coerce that to a vector of long values, or produce `:clojure.spec.alpha/invalid` on bad input:
* `split->longs`

## Releases

0.1.0 -- Sep 20, 2019 -- first public release.

## License

Copyright © 2017-2019 [World Singles Networks llc](https://worldsinglesnetworks.com/).

Distributed under the Eclipse Public License version 1.0.
