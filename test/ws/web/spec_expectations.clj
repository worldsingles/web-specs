;; copyright (c) 2018-2019 world singles networks llc

(ns ws.web.spec-expectations
  (:require [clojure.spec.alpha :as s]
            [expectations.clojure.test :refer [approximately
                                               defexpect expect expecting
                                               from-each in more-of]]
            [java-time :as jt]
            [ws.web.spec :refer :all]))

(defexpect boolean-tests
  (expecting "String conversions"
    (expect true (->boolean "true"))
    (expect true (->boolean "TRUE"))
    (expect false (->boolean "false"))
    (expect false (->boolean "FALSE")))
  (expecting "Passthrough"
    (expect true (->boolean true))
    (expect false (->boolean false)))
  (expecting "Invalid input"
    (expect s/invalid? (->boolean "not a boolean"))
    (expect s/invalid? (->boolean :not-a-boolean))
    (expect s/invalid? (s/conform :ws.web.spec/boolean "not a boolean"))
    (expect s/invalid? (s/conform :ws.web.spec/boolean :not-a-boolean)))
  (expecting "Conformance"
    (expect :ws.web.spec/boolean "true")
    (expect :ws.web.spec/boolean "TRUE")
    (expect :ws.web.spec/boolean "false")
    (expect :ws.web.spec/boolean "FALSE")
    (expect :ws.web.spec/boolean true)
    (expect :ws.web.spec/boolean false))
  (expecting "Exercise"
    (expect (more-of [s v]
              string? s
              boolean? v
              true (= s (str v))
              s (in #{"true" "false"}))
      (from-each [pair (s/exercise :ws.web.spec/boolean)]
        pair))))

(defexpect long-tests
  (expecting "String conversions"
    (expect -123 (->long "-123"))
    (expect 456 (->long "456"))
    (expect 0 (->long "0")))
  (expecting "Passthrough"
    (expect -333 (->long -333))
    (expect 666 (->long 666)))
  (expecting "Invalid input"
    (expect s/invalid? (->long "not a long"))
    (expect s/invalid? (->long :not-a-long))
    (expect s/invalid? (s/conform :ws.web.spec/long ""))
    (expect s/invalid? (s/conform :ws.web.spec/long nil))
    (expect s/invalid? (s/conform :ws.web.spec/long "not a long"))
    (expect s/invalid? (s/conform :ws.web.spec/long :not-a-long)))
  (expecting "Conformance"
    (expect :ws.web.spec/long "-1")
    (expect :ws.web.spec/long "10203040")
    (expect :ws.web.spec/long "0")
    (expect :ws.web.spec/long "-999")
    (expect :ws.web.spec/long -1234)
    (expect :ws.web.spec/long 4321))
  (expecting "Exercise"
    (expect (more-of [s v]
              string? s
              int? v
              true (= s (str v)))
      (from-each [pair (s/exercise :ws.web.spec/long)]
        pair))))

(defexpect pos-int-tests
  (expecting "Invalid input"
    (expect s/invalid? (s/conform :ws.web.spec/pos-int "0"))
    (expect s/invalid? (s/conform :ws.web.spec/pos-int "-100"))
    (expect s/invalid? (s/conform :ws.web.spec/pos-int 0))
    (expect s/invalid? (s/conform :ws.web.spec/pos-int -123))
    (expect s/invalid? (s/conform :ws.web.spec/pos-int "not a long"))
    (expect s/invalid? (s/conform :ws.web.spec/pos-int :not-a-long)))
  (expecting "Conformance"
    (expect :ws.web.spec/pos-int "10203040")
    (expect :ws.web.spec/pos-int 4321))
  (expecting "Exercise"
    (expect (more-of [s v]
              string? s
              int? v
              pos-int? v
              true (= s (str v)))
      (from-each [pair (s/exercise :ws.web.spec/pos-int)]
        pair))))

(defexpect opt-long-tests
  (expecting "Invalid input"
    (expect s/invalid? (s/conform :ws.web.spec/opt-long "not a long"))
    (expect s/invalid? (s/conform :ws.web.spec/opt-long :not-a-long)))
  (expecting "Conformance"
    (expect :ws.web.spec/opt-long "")
    (expect :ws.web.spec/opt-long nil)
    (expect :ws.web.spec/opt-long "-1")
    (expect :ws.web.spec/opt-long "10203040")
    (expect :ws.web.spec/opt-long "0")
    (expect :ws.web.spec/opt-long "-999")
    (expect :ws.web.spec/opt-long -1234)
    (expect :ws.web.spec/opt-long 4321))
  (expecting "Exercise"
    (expect (more-of [s v]
              true (or (string? s) (nil? s))
              true (or (int? v) (nil? v))
              true (or (= s (str v))
                     (= nil s v)))
      (from-each [pair (s/exercise :ws.web.spec/opt-long)]
        pair))))

(defexpect age-tests
  (expecting "Invalid input"
    (expect s/invalid? (s/conform :ws.web.spec/age "0"))
    (expect s/invalid? (s/conform :ws.web.spec/age "15"))
    (expect s/invalid? (s/conform :ws.web.spec/age "17"))
    (expect s/invalid? (s/conform :ws.web.spec/age "-100"))
    (expect s/invalid? (s/conform :ws.web.spec/age "121"))
    (expect s/invalid? (s/conform :ws.web.spec/age "200"))
    (expect s/invalid? (s/conform :ws.web.spec/age 0))
    (expect s/invalid? (s/conform :ws.web.spec/age -123))
    (expect s/invalid? (s/conform :ws.web.spec/age "not a long"))
    (expect s/invalid? (s/conform :ws.web.spec/age :not-a-long)))
  (expecting "Conformance"
    (expect :ws.web.spec/age "18")
    (expect :ws.web.spec/age "55")
    (expect :ws.web.spec/age "120")
    (expect :ws.web.spec/age 32))
  (expecting "Exercise"
    (expect (more-of [s v]
              string? s
              int? v
              true (= s (str v))
              true (<= 18 v 120))
      (from-each [pair (s/exercise :ws.web.spec/age)]
        pair))))

(defexpect double-tests
  (expecting "String conversions"
    (expect (approximately -123) (->double "-123"))
    (expect (approximately 456.78) (->double "456.78"))
    (expect (approximately 0) (->double "0")))
  (expecting "Passthrough"
    (expect (approximately -333.0) (->double -333.0))
    (expect (approximately 666.666) (->double 666.666))
    (expect (approximately 123.0) (->double 1.23e2)))
  (expecting "Invalid input"
    (expect s/invalid? (->double "not a double"))
    (expect s/invalid? (->double :not-a-double))
    (expect s/invalid? (s/conform :ws.web.spec/double ""))
    (expect s/invalid? (s/conform :ws.web.spec/double nil))
    (expect s/invalid? (s/conform :ws.web.spec/double "not a double"))
    (expect s/invalid? (s/conform :ws.web.spec/double :not-a-double)))
  (expecting "Conformance"
    (expect :ws.web.spec/double "-1")
    (expect :ws.web.spec/double "10203.040")
    (expect :ws.web.spec/double "0")
    (expect :ws.web.spec/double "-99.9")
    (expect :ws.web.spec/double -12.34)
    (expect :ws.web.spec/double 4.321))
  (expecting "Exercise"
    (expect (more-of [s v]
              string? s
              double? v
              true (= s (str v)))
      (from-each [pair (s/exercise :ws.web.spec/double)]
        pair)))
  (expecting "Optional doubles"
    (expect :ws.web.spec/opt-double "")
    (expect :ws.web.spec/opt-double nil)
    (expect (more-of [s v]
              true (or (string? s) (nil? s))
              true (or (double? v) (nil? v))
              true (or (= s (str v))
                     (= nil s v)))
      (from-each [pair (s/exercise :ws.web.spec/opt-double)]
        pair))))

(defexpect date-tests
  (expecting "String conversions"
    (expect (jt/sql-date 1962 7 7) (->date "1962/7/7"))
    (expect (jt/sql-date 1962 7 7) (->date "1962-7-7"))
    (expect (jt/sql-date 1960 11 26) (->date "11/26/1960"))
    (expect (jt/sql-date 1960 11 26) (->date "11-26-1960"))
    ;; watch out for short year -- it will always be 2000+
    (expect (jt/sql-date 2060 11 26) (->date "11/26/60")))
  (expecting "Passthrough"
    (expect (jt/sql-date 2019 9 20) (->date (jt/sql-date 2019 9 20)))
    (let [now (java.util.Date.)]
      (expect now (->date now))))
  (expecting "Invalid input"
    (expect s/invalid? (->date "not a date"))
    (expect s/invalid? (->date "99/99/99"))
    (expect s/invalid? (->date :not-a-date))
    (expect s/invalid? (s/conform :ws.web.spec/date ""))
    (expect s/invalid? (s/conform :ws.web.spec/date nil))
    (expect s/invalid? (s/conform :ws.web.spec/date "not a long"))
    (expect s/invalid? (s/conform :ws.web.spec/date :not-a-long)))
  (expecting "Conformance"
    (expect :ws.web.spec/date "2019/9/20")
    (expect :ws.web.spec/date "7/7/62")
    (expect :ws.web.spec/date "Fri Sep 20 12:34:56 PDT 2019")
    (expect :ws.web.spec/date (java.util.Date.))
    (expect :ws.web.spec/date #inst "2000-01-01"))
  (expecting "Exercise"
    (expect (more-of [s v]
              string? s
              inst? v
              #"[01][0-9]/[0123][0-9]/[0-9]{4}" s)
      (from-each [pair (s/exercise :ws.web.spec/date)]
        pair)))
  (expecting "Optional dates"
    (expect nil? (s/conform :ws.web.spec/opt-date ""))
    (expect nil? (s/conform :ws.web.spec/opt-date nil))
    (expect (more-of [s v]
              true (or (string? s) (nil? s))
              true (or (inst? v) (nil? v))
              some? (or (nil? s)
                      (re-find #"[01][0-9]/[0123][0-9]/[0-9]{4}" s)))
      (from-each [pair (s/exercise :ws.web.spec/opt-date)]
        pair))))

(defexpect date-time-tests
  (expecting "String conversions"
    (expect #inst "2020-04-05T22:28:12.000" (->date-time "2020-04-05T22:28:12.000Z"))
    (expect #inst "2020-12-31T23:59:59.999" (->date-time "2020-12-31T23:59:59.999Z")))
  (expecting "Invalid input"
    (expect s/invalid? (->date-time "2020/04/05T22:28:12.000Z"))
    (expect s/invalid? (->date-time "2020-4-5T22:28:12.000Z"))
    (expect s/invalid? (->date-time "2020/04/05"))
    (expect s/invalid? (->date-time "99/99/99T99:99:99.999Z"))
    (expect s/invalid? (->date-time "22:40:30.231Z"))
    (expect s/invalid? (->date-time "not a date time"))
    (expect s/invalid? (->date-time :not-a-date-time))
    (expect s/invalid? (s/conform :ws.web.spec/date-time ""))
    (expect s/invalid? (s/conform :ws.web.spec/date-time nil)))
  (expecting "Conformance"
    (expect :ws.web.spec/date-time "2020-01-01T00:00:00.000Z")
    (expect :ws.web.spec/date-time (java.util.Date.))
    (expect :ws.web.spec/date-time #inst "2010-11-03T15:34:22.033Z"))
  (expecting "Exercise"
    (expect (more-of [s v]
              string? s
              inst? v
              #"[0-9]{4}-[01][0-9]-[0123][0-9]T[012][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}Z" s)
      (from-each [pair (s/exercise :ws.web.spec/date-time)]
        pair)))
  (expecting "Optional date times"
    (expect nil? (s/conform :ws.web.spec/opt-date-time ""))
    (expect nil? (s/conform :ws.web.spec/opt-date-time nil))
    (expect (more-of [s v]
              true (or (string? s) (nil? s))
              true (or (inst? v) (nil? v))
              some? (or (nil? s)
                      (re-find #"[0-9]{4}-[01][0-9]-[0123][0-9]T[012][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}Z" s)))
      (from-each [pair (s/exercise :ws.web.spec/opt-date-time)]
        pair))))
