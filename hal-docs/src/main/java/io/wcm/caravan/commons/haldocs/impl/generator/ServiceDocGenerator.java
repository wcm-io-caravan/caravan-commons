/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.caravan.commons.haldocs.impl.generator;

import io.wcm.caravan.commons.haldocs.model.LinkRelation;
import io.wcm.caravan.commons.haldocs.model.Service;
import io.wcm.caravan.commons.stream.Streams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

/**
 * Generate HTML documentation from model.
 */
public class ServiceDocGenerator {

  private static final String CLASSPATH_TEMPLATES = "HALDOCS-TEMPLATE-INF/templates";
  private static final String CLASSPATH_FRONTEND = "HALDOCS-TEMPLATE-INF/frontend";

  private final Handlebars handlebars;
  private final Template serviceTemplate;
  private final Template linkRelationTemplate;

  private final ClassPath classPath;

  /**
   * Initialize generator.
   * @throws IOException
   */
  public ServiceDocGenerator() throws IOException {
    TemplateLoader templateLoader = new ClassPathTemplateLoader("/" + CLASSPATH_TEMPLATES, "");
    this.handlebars = new Handlebars(templateLoader);

    this.serviceTemplate = handlebars.compile("service.html.hbs");
    this.linkRelationTemplate = handlebars.compile("linkRelation.html.hbs");

    this.classPath = ClassPath.from(getClass().getClassLoader());
  }

  /**
   * Generate HTML documentation.
   * @param service Service model
   * @param targetDir Target directory.
   */
  public void generate(Service service, File targetDir) {
    generateHtml(service, targetDir);
    copyFrontend(targetDir);
  }

  /**
   * Generate HTML files.
   * @param service Service
   * @param targetDir Target directory
   */
  private void generateHtml(Service service, File targetDir) {
    generateServiceHtml(service, targetDir);
    Streams.of(service.getLinkRelations()).forEach(rel -> generateLinkRelationHtml(service, rel, targetDir));
  }

  /**
   * Generate HTML file for service.
   * @param service Service
   * @param targetDir Target directory
   */
  private void generateServiceHtml(Service service, File targetDir) {
    File targetFile = new File(targetDir, service.getFilename());
    Map<String, Object> model = ImmutableMap.<String, Object>builder()
        .put("service", service)
        .build();
    generateTemplatedFile(model, serviceTemplate, targetFile);
  }

  /**
   * Generate HTML file for link relation.
   * @param service Service
   * @param linkRelation Link relation
   * @param targetDir Target directory
   */
  private void generateLinkRelationHtml(Service service, LinkRelation linkRelation, File targetDir) {
    File targetFile = new File(targetDir, linkRelation.getFilename());
    Map<String, Object> model = ImmutableMap.<String, Object>builder()
        .put("service", service)
        .put("linkRelation", linkRelation)
        .build();
    generateTemplatedFile(model, linkRelationTemplate, targetFile);
  }

  /**
   * Generate templated file with handlebars
   * @param model Model
   * @param template Template
   * @param targetFile Target file
   */
  private void generateTemplatedFile(Map<String, Object> model, Template template, File targetFile) {
    ensureDirectoryExists(targetFile);
    try (Writer writer = new FileWriterWithEncoding(targetFile, CharEncoding.UTF_8)) {
      template.apply(model, writer);
    }
    catch (IOException ex) {
      throw new RuntimeException("Unable to generate file: " + targetFile.getPath(), ex);
    }
  }

  /**
   * Copy all static frontend resources
   * @param targetDir Target directory
   */
  private void copyFrontend(File targetDir) {
    Streams.of(classPath.getResources())
    .filter(info -> StringUtils.startsWith(info.getResourceName(), CLASSPATH_FRONTEND))
    .forEach(info -> copyFrontendFile(info, targetDir));
  }

  /**
   * Copy one static frontend resource file
   * @param resourceInfo Resource
   * @param targetDir Target directory
   */
  private void copyFrontendFile(ResourceInfo resourceInfo, File targetDir) {
    String relativePath = StringUtils.substringAfter(resourceInfo.getResourceName(), CLASSPATH_FRONTEND);
    File targetFile = new File(targetDir, relativePath);
    ensureDirectoryExists(targetFile);
    try (InputStream is = resourceInfo.url().openStream();
        OutputStream os = new FileOutputStream(targetFile)) {
      IOUtils.copy(is, os);
    }
    catch (IOException ex) {
      throw new RuntimeException("Error copying file " + resourceInfo.getResourceName() + " to " + targetFile.getPath(), ex);
    }
  }

  /**
   * Ensure directory of file exists, creates it if not.
   * @param file File
   */
  private void ensureDirectoryExists(File file) {
    File parent = file.getParentFile();
    if (!parent.exists()) {
      parent.mkdirs();
    }
  }

}