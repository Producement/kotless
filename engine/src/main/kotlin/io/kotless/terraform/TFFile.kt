package io.kotless.terraform

import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLTextArrayField
import io.kotless.terraform.infra.TFLocals
import java.io.File

/** Representation of file with Terraform code */
class TFFile(val name: String, private val entities: MutableList<HCLEntity.Named> = ArrayList()) : Comparable<TFFile> {
    private val nameWithExt = "$name.tf"

    fun writeToDirectory(directory: File): File {
        require(directory.exists().not() || directory.isDirectory) { "TFFile can be written only to directory" }

        directory.mkdirs()

        val file = File(directory, nameWithExt)
        if (!file.exists()) file.createNewFile()

        file.writeText(buildString {
            val resources = mutableMapOf<String, MutableSet<String>>()
            for (entity in entities.sorted()) {
                if (entity is TFResource) {
                    resources.getOrPut(entity.tf_type) { mutableSetOf() }.add(entity.tf_id)
                }
                append(entity.render())
                append("\n\n")
            }
            append(TFLocals().also {
                it.variables = object : HCLEntity() {
                    init {
                        for ((key, values) in resources) {
                            fields.add(HCLTextArrayField(key, false, this, values.toTypedArray()))
                        }
                    }
                }
            }.render())
            append("\n\n")
        })

        return file
    }

    fun add(entity: HCLEntity.Named) {
        entities.add(entity)
    }

    override fun compareTo(other: TFFile): Int {
        return name.compareTo(other.name)
    }
}

fun tf(name: String, configure: TFFile.() -> Unit) = TFFile(name).apply(configure)
