/* Copyright 2016 Mikolaj Stefaniak
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package pl.surreal.finance.transaction;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.metrics.graphite.GraphiteReporterFactory;

public class TransactionConfiguration extends Configuration
{
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
    
    @Valid
    private GraphiteReporterFactory graphiteReporterFactory = new GraphiteReporterFactory();

    @Valid
    private SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
    
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }    
    
    @JsonProperty("metrics")
    public GraphiteReporterFactory getGraphiteReporterFactory() {
        return graphiteReporterFactory;
    }

    @JsonProperty("metrics")
    public void setGraphiteReporterFactory(GraphiteReporterFactory graphiteReporterFactory) {
        this.graphiteReporterFactory = graphiteReporterFactory;
    }
    
    @JsonProperty("swagger")
    public SwaggerConfiguration getSwaggerConfiguration() {
    	return this.swaggerConfiguration;
    }
    
    @JsonProperty("swagger")
    public void setSwaggerConfiguration(SwaggerConfiguration swaggerConfiguration) {
    	this.swaggerConfiguration = swaggerConfiguration;
    }
}
