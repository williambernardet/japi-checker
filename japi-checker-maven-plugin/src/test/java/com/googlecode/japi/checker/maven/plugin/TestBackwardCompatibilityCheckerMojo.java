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
import java.util.Collections;

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

public class TestBackwardCompatibilityCheckerMojo extends AbstractMojoTestCase {
    private BackwardCompatibilityCheckerMojo mojo;
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
        project.setCompileArtifacts(Collections.EMPTY_LIST);
        project.setGroupId("com.googlecode.japi-checker");
        project.setArtifactId("reference-test-jar");
        project.setVersion("0.1.1-SNAPSHOT");
        
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
     * Identity check should not fail.
     * @throws MojoExecutionException
     * @throws IllegalAccessException
     * @throws MojoFailureException
     */
    public void testValidationWithSameJar() throws MojoExecutionException, IllegalAccessException, MojoFailureException {
        ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId(mojo.getProject().getGroupId());
        artifact.setArtifactId(mojo.getProject().getArtifactId());
        artifact.setVersion(mojo.getProject().getVersion());
        artifact.setType("jar");
        artifact.setScope(Artifact.SCOPE_RUNTIME);
        artifact.setFile(new File( "src/test/repository/com/googlecode/japi-checker/reference-test-jar/0.1.0/reference-test-jar-0.1.0.jar"));
        mojo.getProject().setArtifact(artifact);
        setVariableValueToObject(mojo, "artifact", artifact);
        mojo.execute();
    }
    
    /**
     * Backward compatibility breaks must fail maven.
     * @throws MojoExecutionException
     * @throws IllegalAccessException
     * @throws MojoFailureException
     */
    public void testValidationWithNewJar() throws MojoExecutionException, IllegalAccessException, MojoFailureException {
        
        ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId(mojo.getProject().getGroupId());
        artifact.setArtifactId(mojo.getProject().getArtifactId());
        artifact.setVersion(mojo.getProject().getVersion());
        artifact.setType("jar");
        artifact.setScope(Artifact.SCOPE_RUNTIME);
        artifact.setFile(new File("src/test/repository/com/googlecode/japi-checker/reference-test-jar/0.1.1-SNAPSHOT/reference-test-jar-0.1.1-SNAPSHOT.jar"));
        mojo.getProject().setArtifact(artifact);
        setVariableValueToObject(mojo, "artifact", artifact);

        try {
            mojo.execute();
            fail("The validation must fail.");
        } catch (MojoFailureException e) {
            // should be there
            assertTrue("You have 2 backward compatibility issues.".equals(e.getMessage()));
        }

    }

    /**
     * Content of the war is the same as the reference one, we just want to make sure it is loaded
     * properly even if the extension is not a war.
     * @throws MojoExecutionException
     * @throws IllegalAccessException
     * @throws MojoFailureException
     */
    public void testValidationWithWar() throws MojoExecutionException, IllegalAccessException, MojoFailureException {
        ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId(mojo.getProject().getGroupId());
        artifact.setArtifactId(mojo.getProject().getArtifactId());
        artifact.setVersion("0.1.1-war-SNAPSHOT");
        artifact.setType("war");
        artifact.setScope(Artifact.SCOPE_RUNTIME);
        artifact.setFile(new File("src/test/repository/com/googlecode/japi-checker/reference-test-jar/0.1.1-war-SNAPSHOT/reference-test-jar-0.1.1-war-SNAPSHOT.war"));
        mojo.getProject().setArtifact(artifact);
        setVariableValueToObject(mojo, "artifact", artifact);
        mojo.execute();
    }

    /**
     * We are verifying that invalid type of archive is failing maven properly.
     * @throws MojoExecutionException
     * @throws IllegalAccessException
     * @throws MojoFailureException
     */
    public void testValidationWithNewInvalidTypeOfFile() throws MojoExecutionException, IllegalAccessException, MojoFailureException {
        ArtifactStub artifact = new ArtifactStub();
        artifact.setGroupId(mojo.getProject().getGroupId());
        artifact.setArtifactId(mojo.getProject().getArtifactId());
        artifact.setVersion("0.1.1-invalid-SNAPSHOT");
        artifact.setType("invalid");
        artifact.setScope(Artifact.SCOPE_RUNTIME);
        artifact.setFile(new File("src/test/repository/com/googlecode/japi-checker/reference-test-jar/0.1.1-invalid-SNAPSHOT/reference-test-jar-0.1.1-invalid-SNAPSHOT.invalid"));
        mojo.getProject().setArtifact(artifact);
        setVariableValueToObject(mojo, "artifact", artifact);

        try {
            mojo.execute();
            fail("The validation must fail.");
        } catch (MojoExecutionException e) {
            // should be there
            assertTrue(e.getMessage().contains("new artifact must be either a directory or a jar"));
        }

    }
    
}
