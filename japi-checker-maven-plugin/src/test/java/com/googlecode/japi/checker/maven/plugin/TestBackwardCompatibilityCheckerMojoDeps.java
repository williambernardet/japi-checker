/*
 * Copyright 2011 William Bernardet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.japi.checker.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.codehaus.plexus.util.FileUtils;

import com.googlecode.japi.checker.maven.plugin.RecorderLog.Kind;

public class TestBackwardCompatibilityCheckerMojoDeps extends AbstractMojoTestCase {
    private BackwardCompatibilityCheckerMojo mojo;
    private File reference;
    private File newVersion;
    private File localRepositoryPath;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prepareRepository();
        ArtifactRepositoryLayout localRepositoryLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE, "default" );
        ArtifactRepository localRepository = new DefaultArtifactRepository( "local", "file://" + localRepositoryPath.getAbsolutePath(), localRepositoryLayout );
                
        mojo = (BackwardCompatibilityCheckerMojo)this.lookupMojo("check",
                new File("target/test-classes/unit/plugin-config.xml"));
        MavenProjectStub project = new MavenProjectStub();
        project.setGroupId("com.googlecode.japi-checker");
        project.setArtifactId("reference-test-jar");
        project.setVersion("0.1.1-SNAPSHOT");
        // The new library has now a dependency.
        List<Artifact> deps = new ArrayList<Artifact>();
        ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId("org.apache.commons");
        artifact.setArtifactId("commons-lang3");
        artifact.setVersion("3.0");
        artifact.setType("jar");
        artifact.setScope(Artifact.SCOPE_RUNTIME);
        artifact.setFile(new File(localRepositoryPath, "org/apache/commons/commons-lang3/3.0/commons-lang3-3.0.jar"));
        deps.add(artifact);
        project.setCompileArtifacts(deps);
        setVariableValueToObject(mojo, "project", project);
        setVariableValueToObject(mojo, "localRepository", localRepository);
    }
    
    @Override
    protected void tearDown() throws Exception {
        try {
            super.tearDown();
        } finally {
            FileUtils.deleteDirectory(localRepositoryPath);
        }
    }
    
    private void prepareRepository() throws IOException {
        localRepositoryPath = File.createTempFile("repository_", "_dir", new File("target"));
        localRepositoryPath.delete();
        localRepositoryPath.mkdirs();
        FileUtils.copyDirectoryStructure(new File("src/test/repository"), localRepositoryPath);
    }

    /**
     * @throws IllegalAccessException 
     * @throws MojoFailureException 
     * @throws MojoExecutionException 
     * 
     */
    public void testThatDependenciesAreDiscovered() throws IllegalAccessException, MojoExecutionException, MojoFailureException {
        /*
         *  <dependency>
         *   <groupId>org.apache.commons</groupId>
         *   <artifactId>commons-lang3</artifactId>
         *   <version>3.0</version>
         * </dependency>
         */
        ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId(mojo.getProject().getGroupId());
        artifact.setArtifactId(mojo.getProject().getArtifactId());
        artifact.setVersion(mojo.getProject().getVersion());
        artifact.setType("jar");
        artifact.setScope(Artifact.SCOPE_RUNTIME);
        artifact.setFile(new File( "src/test/repository/com/googlecode/japi-checker/reference-test-jar/0.1.0/reference-test-jar-0.1.0.jar"));
        mojo.getProject().setArtifact(artifact);
        setVariableValueToObject(mojo, "artifact", artifact);
        RecorderLog log = new RecorderLog(mojo);
        mojo.execute();
        assertTrue(log.contains(Kind.DEBUG, "Adding new artifact dependency: "));
        assertTrue(log.contains(Kind.DEBUG, "commons-lang3-3.0.jar"));
    }
}
