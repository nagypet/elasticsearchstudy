# ElasticSearch sample with Spring boot 3.0.4

Inspired by Csaba Marton https://github.com/csabamarton/spring-elasticsearch

## User database

1. start the elasticseach database in docker. Please check out the docker-compose folder.

```
CONTAINER ID   IMAGE                         COMMAND                  CREATED         STATUS       PORTS                              NAMES        
7cdc774a7c88   elasticsearch:8.5.3           "/bin/tini -- /usr/l…"   8 hours ago     Up 8 hours   0.0.0.0:9200->9200/tcp, 9300/tcp   elasticsearch
```
2. Start Spring Boot Application
3. Use Postman to test the application. Please see the postman folder.

- POST localhost:8700/api/elasticsearch/user
- GET localhost:8700/api/elasticsearch/user
- POST localhost:8700/api/elasticsearch/user/search

## Address database

The AddressEntity looks like:
```java
@Getter
@Setter
@ToString
@Document(indexName = AddressEntity.INDEX_NAME)
public class AddressEntity
{
    public static final String INDEX_NAME = "address";
    public static final String FIELD_POSTCODE = "postcode";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_STREET = "street";
    public static final String FIELD_DISTRICT = "district";
    public static final String FIELD_SEARCH = "search";

    @Id
    private String uuid;

    @Field(type = FieldType.Text, name = FIELD_POSTCODE)
    private String postCode;

    @Field(type = FieldType.Text, name = FIELD_CITY)
    private String city;

    @Field(type = FieldType.Text, name = FIELD_STREET)
    private String street;

    @Field(type = FieldType.Text, name = FIELD_DISTRICT)
    private String district;

    @Field(type = FieldType.Text, name = FIELD_SEARCH)
    private String searchField;

    public void createSearchField()
    {
        this.searchField = textField(this.postCode) + textField(this.city) + textField(this.street) + textField(this.district);
    }

    private String textField(String text)
    {
        if (StringUtils.isBlank(text))
        {
            return "";
        }
        return text.replaceAll("\\s", "");
    }
}
```
An extra field has been introduced for searching, because I wanted that a partial match works in each field of the document. The search field contains the textual representation of each content field without any whitespace. In that case a search like `1038 kert` delivers the right match, where different fields of the document have to be search for with an allMatch condition.

There are around 10k addresses in the file addresses_budapest.csv. Loading:
- POST http://localhost:8700/api/elasticsearch/address/load

Searching:
- POST http://localhost:8700/api/elasticsearch/address/search

Searching for 'kert'
```json
{
  "from": 0,
  "post_filter": {
    "bool": {}
  },
  "query": {
    "bool": {
      "must": [
        {
          "query_string": {
            "analyze_wildcard": true,
            "fields": [
              "search"
            ],
            "query": "*kert*"
          }
        }
      ]
    }
  },
  "size": 100,
  "track_scores": false,
  "version": true
}
```

Searching for 'kert sor'
```json
{
  "from": 0,
  "query": {
    "bool": {
      "should": [
        {
          "bool": {
            "must": [
              {
                "query_string": {
                  "analyze_wildcard": true,
                  "fields": [
                    "search"
                  ],
                  "query": "*kert*"
                }
              },
              {
                "query_string": {
                  "analyze_wildcard": true,
                  "fields": [
                    "search"
                  ],
                  "query": "*sor*"
                }
              }
            ]
          }
        }
      ]
    }
  },
  "size": 100,
  "track_scores": false,
  "version": true
}
```
