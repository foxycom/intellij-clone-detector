package com.github.foxycom.intellijclonedetector.services

import com.github.foxycom.intellijclonedetector.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
