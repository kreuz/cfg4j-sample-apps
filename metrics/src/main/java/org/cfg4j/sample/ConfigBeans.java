/*
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfg4j.sample;

import com.codahale.metrics.MetricRegistry;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
public class ConfigBeans {

  @Value("${configFilesPath:./metrics/build/libs/}")
  private String filesPath; // Run with -DconfigFilesPath=<configFilesPath> parameter to override

  @Autowired
  private MetricRegistry metricRegistry;

  @Bean
  public ConfigurationProvider configurationProvider() {
    // Specify which files to load. Configuration from both files will be merged.
    ConfigFilesProvider configFilesProvider = () -> Collections.singletonList(Paths.get("application.yaml"));

    // Use local files as configuration store
    ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);

    // Use relative paths
    Environment environment = new ImmutableEnvironment(filesPath);

    // Reload configuration every 500 milliseconds
    ReloadStrategy refreshStrategy = new PeriodicalReloadStrategy(500, TimeUnit.MILLISECONDS);

    // Create provider
    return new ConfigurationProviderBuilder()
        .withConfigurationSource(source)
        .withReloadStrategy(refreshStrategy)
        .withEnvironment(environment)
        .withMetrics(metricRegistry, "firstProvider.")
        .build();
  }
}
