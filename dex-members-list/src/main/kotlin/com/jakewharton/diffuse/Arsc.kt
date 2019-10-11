package com.jakewharton.diffuse

import com.google.devrel.gmscore.tools.apk.arsc.ArscBlamer
import com.google.devrel.gmscore.tools.apk.arsc.BinaryResourceFile
import com.google.devrel.gmscore.tools.apk.arsc.ResourceTableChunk
import okio.ByteString

class Arsc private constructor(
  val configs: List<String>,
  val entries: List<String>
) {
  companion object {
    @JvmStatic
    @JvmName("create")
    fun ByteString.toArsc(): Arsc {
      val resourceFile = BinaryResourceFile(toByteArray())
      val chunk = resourceFile.chunks.single()
      check(chunk is ResourceTableChunk) { "Root arsc chunk is not a resource table "}
      val arscBlamer = ArscBlamer(chunk).apply { blame() }
      val configs = arscBlamer.typeChunks.map { it.typeName }
      // TODO map this to model object?
      val entries = arscBlamer.resourceEntries.keySet().map { "@${it.typeName()}/${it.entryName()}" }
      return Arsc(configs, entries)
    }
  }
}
