;; copyright (c) 2017-2019 world singles llc

(ns ws.web.spec
  "Reusable specs and spec-related utilities, common to web apps."
  (:require [clojure.spec-alpha2 :as s]
            [clojure.spec-alpha2.gen :as g]
            [clojure.string :as str]
            [java-time :as jt]))

;; generic specs to conform from strings

(defn ->boolean
  "Spec predicate: conform to Boolean else invalid."
  [s]
  (if (boolean? s)
    s
    ({"true" true "false" false} (str/lower-case s) ::s/invalid)))

(defn coerce->long
  "Given a string or number, produce a long, or throw an exception.
  Low level utility used by spec predicates to accept either a
  number or a string that can be converted to a number."
  [s]
  (if (number? s) s (Long/parseLong s)))

(defn ->long
  "Spec predicate: conform to Long else invalid."
  [s]
  (try (coerce->long s)
       (catch Exception _ ::s/invalid)))

(defn coerce->double
  "Given a string or number, produce a double, or throw an exception.
  Low level utility used by spec predicates to accept either a
  number or a string that can be converted to a number."
  [s]
  (if (number? s) s (Double/parseDouble s)))

(defn ->double
  "Spec predicate: conform to Double else invalid."
  [s]
  (try (coerce->double s)
       (catch Exception _ ::s/invalid)))

(defn coerce->date
  "Given a string or date, produce a date, or throw an exception.
  Low level utility used by spec predicates to accept either a
  date or a string that can be converted to a date."
  [s]
  (if (instance? java.util.Date s)
    s
    (jt/sql-date
     (some #(try
              (jt/local-date % s)
              (catch Exception _))
           ["yyyy/M/d" "M/d/yyyy"
            "yyyy-M-d" "M-d-yyyy"
            "M/d/yy" "M-d-yy"
            "EEE MMM dd HH:mm:ss zzz yyyy"]))))

(defn ->date
  "Spec predicate: conform to Date else invalid."
  [s]
  (try (coerce->date s)
       (catch Exception _ ::s/invalid)))

(defn split->longs
  "Spec predicate: conform string to collection of Long.
  Also accepts a collection of numbers."
  [s]
  (try (mapv coerce->long (if (string? s) (str/split s #",") s))
       (catch Exception _ ::s/invalid)))

(defmacro param-spec
  "Given a coercion function and a predicate / spec, produce a
  spec that accepts strings that can be coerced to a value that
  satisfies the predicate / spec, and will also generate strings
  that conform to the given spec."
  [coerce str-or-spec & [spec]]
  (let [[to-str spec] (if spec [str-or-spec spec] [str str-or-spec])]
    `(s/with-gen (s/and (s/conformer ~coerce) ~spec)
       (fn [] (g/fmap ~to-str (s/gen (s/spec ~spec)))))))

(defmacro opt-param-spec
  "Given a coercion function and a predicate / spec, produce a
  spec that accepts strings that can be coerced to a value that
  satisfies the predicate / spec, and will also generate strings
  that conform to the given spec.
  An empty string is coerced to nil; a generated nil produces the
  empty string."
  [coerce str-or-spec & [spec]]
  (let [[to-str spec] (if spec [str-or-spec spec] [str str-or-spec])]
    `(s/with-gen (s/and (s/or :n nil?
                              :e (s/and string? empty? (s/conformer seq))
                              :s (s/and (s/conformer ~coerce) ~spec))
                        (s/conformer val))
       (fn [] (g/frequency [[1 (s/gen (s/spec nil?))]
                            [5 (g/fmap ~to-str (s/gen (s/spec ~spec)))]])))))

(s/register ::boolean (param-spec ->boolean boolean?))

(s/register ::long (param-spec ->long int?))

(s/register ::opt-long (opt-param-spec ->long int?))

(s/register ::pos-int (param-spec ->long pos-int?))

(s/register ::age (param-spec ->long (s/int-in 18 121)))

(s/register ::double (param-spec ->double double?))

(s/register ::opt-double (opt-param-spec ->double double?))

(s/register ::date (param-spec ->date
                               #(jt/format "MM/dd/yyyy" (jt/offset-date-time % 0))
                               inst?))

(s/register ::opt-date (opt-param-spec ->date
                                       #(jt/format "MM/dd/yyyy" (jt/offset-date-time % 0))
                                       inst?))
