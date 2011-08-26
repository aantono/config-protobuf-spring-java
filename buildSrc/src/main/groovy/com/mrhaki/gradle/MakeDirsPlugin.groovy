// File: buildSrc/src/main/groovy/com/mrhaki/gradle/MakeDirsPlugin.groovy
package com.mrhaki.gradle

import org.gradle.api.*
import org.gradle.api.plugins.*

class MakeDirsPlugin implements Plugin {

    void use(final Project project, ProjectPluginsContainer plugins) {
        // Closure to create a directory.
        def createDirs = {
            it.mkdirs()
        }

        // Create new task 'mkdirs' and add it to the project.
        project.task('mkdirs') << {
            if (plugins.hasPlugin('java')) {
                project.sourceSets.all.java.srcDirs*.each createDirs
                project.sourceSets.all.resources.srcDirs*.each createDirs
            }
            
            if (plugins.hasPlugin('groovy')) {
                project.sourceSets.all.groovy.srcDirs*.each createDirs
            }
            
            if (plugins.hasPlugin('scala')) {
                project.sourceSets.all.scala.srcDirs*.each createDirs
            }
            
            if (plugins.hasPlugin('war')) {
                createDirs project.webAppDir
            }
        }
        // Assign a description to the task.
        project.tasks.mkdirs.description = "Create source directories."
    }
    
}
