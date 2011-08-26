package ws.antonov.gradle.protobuf;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ProjectPluginsContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.tasks.ConventionValue;
import org.gradle.api.plugins.Convention;
import org.gradle.api.internal.IConventionAware;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.internal.DynamicObjectAware;
import org.gradle.api.plugins.JavaPluginConvention;

import java.io.File;

/**
 *
 */

public class ProtobufPlugin implements Plugin {
    public static final String PROTOBUF_CONFIGURATION_NAME = "protobuf";

    public void use(Project project, ProjectPluginsContainer projectPluginsHandler) {
        JavaPlugin javaPlugin = projectPluginsHandler.usePlugin(JavaPlugin.class, project);

        //Configuration groovyConfiguration = project.getConfigurations().add(PROTOBUF_CONFIGURATION_NAME).setVisible(false).setTransitive(false).
        //        setDescription("The groovy libraries to be used for this Groovy project.");
        //project.getConfigurations().getByName(COMPILE_CONFIGURATION_NAME).extendsFrom(groovyConfiguration);

        //configureCompileDefaults(project);
        configureSourceSetDefaults(project, javaPlugin);
    }

    private void configureCompileDefaults(final Project project) {
        project.getTasks().withType(ProtobufCompile.class).allTasks(new Action<ProtobufCompile>() {
            public void execute(ProtobufCompile compile) {
                compile.conventionMapping("protocPath", new ConventionValue() {
                    public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                        return mainProtobuf(convention).getProtocPath();
                    }
                });
            }
        });
    }

    private void configureSourceSetDefaults(final Project project, final JavaPlugin javaPlugin) {
        final ProjectInternal projectInternal = (ProjectInternal) project;
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().allObjects(new Action<SourceSet>() {
            public void execute(final SourceSet sourceSet) {
                final DefaultProtobufSourceSet protobufSourceSet = new DefaultProtobufSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), projectInternal.getFileResolver());
            //    ((DynamicObjectAware) sourceSet).getConvention().getPlugins().put("protobuf", protobufSourceSet);

                protobufSourceSet.getProtobuf().srcDir(String.format("src/%s/proto", sourceSet.getName()));
                sourceSet.getJava().srcDir(String.format("%s/proto-generated/%s", project.getBuildDir(), sourceSet.getName()));
                sourceSet.getResources().getFilter().exclude("**/*.proto");
                //sourceSet.getAllJava().add(protobufSourceSet.getProtobuf().matching(sourceSet.getJava().getFilter()));
                //sourceSet.getAllSource().add(protobufSourceSet.getProtobuf());

                String compileTaskName = sourceSet.getCompileTaskName("proto");
                ProtobufCompile compileProtobufTask = project.getTasks().add(compileTaskName, ProtobufCompile.class);

                String compileJavaTaskName = sourceSet.getCompileTaskName("java");
                Task compileJavaTask = project.getTasks().getByName(compileJavaTaskName);

                javaPlugin.configureForSourceSet(sourceSet, compileProtobufTask);
                compileJavaTask.dependsOn(compileProtobufTask);
                compileProtobufTask.setDescription(String.format("Compiles the %s Protobuf source.", sourceSet.getName()));
                compileProtobufTask.conventionMapping("defaultSource", new ConventionValue() {
                    public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                        return protobufSourceSet.getProtobuf();
                    }
                });

                compileProtobufTask.conventionMapping("destinationDir", new ConventionValue() {
                    public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                        return new File(String.format("%s/proto-generated/%s", project.getBuildDir(), sourceSet.getName()));
                    }
                });

                compileProtobufTask.conventionMapping("protocPath", new ConventionValue() {
                    public Object getValue(Convention convention, IConventionAware conventionAwareObject) {
                        return protobufSourceSet.getProtocPath();
                    }
                });

                project.getTasks().getByName(sourceSet.getClassesTaskName()).dependsOn(compileTaskName);

                Convention convention = project.getConvention();
                convention.getPlugins().put("protobuf", protobufSourceSet);                
            }
        });
    }

    private SourceSet main(Convention convention) {
        return convention.getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }

    private DefaultProtobufSourceSet mainProtobuf(Convention convention) {
        return ((DynamicObjectAware) main(convention)).getConvention().getPlugin(DefaultProtobufSourceSet.class);
    }
}