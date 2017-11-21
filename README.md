# solr-statsd-reporter
StatsD metric reporter for Solr, using StatsDReporter for codahale from ReadyTalk/metrics-statsd

Usage: Add the metric reporter in your Solr.xml
```
<reporter name="statsd" class="com.myntra.solr.statsd.reporter.SolrStatsDReporter">
  <str name="host">127.0.0.1</str>
  <int name="port">8125</int>
  <int name="period">60</int>
  <str name="prefix">solr-${host:}</str>
</reporter>
```
