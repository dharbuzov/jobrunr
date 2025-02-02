package org.jobrunr.tests.e2e;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.nosql.elasticsearch.ElasticSearchStorageProvider;
import org.testcontainers.containers.Network;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticSearchGsonBackgroundJobContainer extends AbstractBackgroundJobContainer {

    private final ElasticsearchContainer elasticSearchContainer;
    private final Network network;

    public ElasticSearchGsonBackgroundJobContainer(ElasticsearchContainer elasticSearchContainer, Network network) {
        super("jobrunr-e2e-elasticsearch-gson:1.0");
        this.elasticSearchContainer = elasticSearchContainer;
        this.network = network;
    }

    @Override
    public void start() {
        this
                .dependsOn(elasticSearchContainer)
                .withNetwork(network)
                .withEnv("ELASTICSEARCH_HOST", "elasticsearch")
                .withEnv("ELASTICSEARCH_PORT", String.valueOf(9200));

        super.start();
    }

    @Override
    public StorageProvider getStorageProviderForClient() {
        HttpHost httpHost = new HttpHost(elasticSearchContainer.getContainerIpAddress(), elasticSearchContainer.getFirstMappedPort(), "http");
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHost).setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setSocketTimeout(100 * 1000)));
        return new ElasticSearchStorageProvider(restHighLevelClient);
    }
}
