curl -XPOST http://cmput301.softwareprocess.es:8080/unter/user/_search -d '{
    "query": {
        "term": {
           "userName": {
              "value": "John"
           }
        }
    }
}'