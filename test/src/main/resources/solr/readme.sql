Download solr from http://www.apache.org/dyn/closer.lua/lucene/solr/7.4.0 and unpack the compressed zip file
From console, Go to {solr_home}\bin\ folder, run "solr start" to start solr server. This action needs to be performed every time after machine restarts.
Hit http://localhost:8983/solr and check if solr is running.
Create a new folder in {solr_home}\server\solr as news_analytics.
Copy solrconfig.xml and conf\managed-schema.xml in news_analytics_files folder.
From postman (or curl), run command - http://localhost:8983/solr/admin/cores?action=CREATE&name=news_analytics&instanceDir=news_analytics_files
Check if core is created on - http://localhost:8983/solr/#/~cores/news_analytics
Now, upload data in created core using command - http://localhost:8983/solr/news_analytics2/update/json/docs