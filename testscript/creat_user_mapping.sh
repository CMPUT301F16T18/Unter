curl -XPUT http://cmput301.softwareprocess.es:8080/unter -d '
{
    "mappings": {
          "user": {
              "properties": {
                  "emailAddress": {
                      "type": "string",
                      "index": "not_analyzed"
                  },
                  "userName": {
                      "type": "string",
                      "index": "not_analyzed"
                  },
                  "mobileNumber": {
                      "type": "string",
                      "index": "not_analyzed"
                }
            }
        }
    }
}'
