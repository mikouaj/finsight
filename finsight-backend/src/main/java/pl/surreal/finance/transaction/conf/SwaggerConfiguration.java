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

package pl.surreal.finance.transaction.conf;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.jaxrs.config.BeanConfig;

public class SwaggerConfiguration
{
	private String title;
	private String description;
	private String termsOfServiceUrl;
	private String contact;
	private String license;
	private String licenseUrl;
	private String version;
	private String[] schemes;
	private String host = "localhost:8080";
	private String basePath = "/";
	private String filterClass;
	private String resourcePackage;
	private boolean scan = false;
	private boolean prettyPrint = false;
	
	public SwaggerConfiguration() { }
	
	@JsonProperty
	public String getTitle() {
		return title;
	}
	
	@JsonProperty
	public void setTitle(String title) {
		this.title = title;
	}
	
	@JsonProperty
	public String getDescription() {
		return description;
	}
	
	@JsonProperty
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty
	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}
	
	@JsonProperty
	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}
	
	@JsonProperty
	public String getContact() {
		return contact;
	}
	
	@JsonProperty
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	@JsonProperty
	public String getLicense() {
		return license;
	}
	
	@JsonProperty
	public void setLicense(String license) {
		this.license = license;
	}
	
	@JsonProperty
	public String getLicenseUrl() {
		return licenseUrl;
	}
	
	@JsonProperty
	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}
	
	@JsonProperty
	public String getVersion() {
		return version;
	}
	
	@JsonProperty
	public void setVersion(String version) {
		this.version = version;
	}
	
	@JsonProperty
	public String[] getSchemes() {
		return schemes;
	}
	
	@JsonProperty
	public void setSchemes(String[] schemes) {
		this.schemes = schemes;
	}
	
	@JsonProperty
	public String getHost() {
		return host;
	}
	
	@JsonProperty
	public void setHost(String host) {
		this.host = host;
	}
	
	@JsonProperty
	public String getBasePath() {
		return basePath;
	}
	
	@JsonProperty
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	@JsonProperty
	public String getFilterClass() {
		return filterClass;
	}
	
	@JsonProperty
	public void setFilterClass(String filterClass) {
		this.filterClass = filterClass;
	}
	
	@JsonProperty
	public String getResourcePackage() {
		return resourcePackage;
	}
	
	@JsonProperty
	public void setResourcePackage(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}
	
	@JsonProperty
	public boolean isScan() {
		return scan;
	}
	
	@JsonProperty
	public void setScan(boolean scan) {
		this.scan = scan;
	}
	
	@JsonProperty
	public boolean isPrettyPrint() {
		return prettyPrint;
	}
	
	@JsonProperty
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	
	public BeanConfig createBeanConfig() {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setTitle(title);
		beanConfig.setDescription(description);
		beanConfig.setTermsOfServiceUrl(termsOfServiceUrl);
		beanConfig.setContact(contact);
		beanConfig.setLicense(license);
		beanConfig.setLicenseUrl(licenseUrl);
		beanConfig.setVersion(version);
		beanConfig.setSchemes(schemes);
		beanConfig.setHost(host);
		beanConfig.setBasePath(basePath);
		beanConfig.setFilterClass(filterClass);
		beanConfig.setResourcePackage(resourcePackage);
		beanConfig.setScan(scan);
		beanConfig.setPrettyPrint(prettyPrint);
		return beanConfig;
	}
}
