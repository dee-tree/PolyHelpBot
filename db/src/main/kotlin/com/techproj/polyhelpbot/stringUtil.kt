package com.techproj.polyhelpbot

import org.apache.commons.text.similarity.LevenshteinDistance

private val dist = LevenshteinDistance.getDefaultInstance()


fun String.minDistance(variants: List<String>): Int {
    return variants.minOf { distance(it) }
}

fun String.minCase(variants: List<String>): String {
    return variants.minByOrNull { distance(it) }!!
}

fun String.distance(other: String, ignoreCase: Boolean = true): Int = dist.apply(
    if (ignoreCase) this.lowercase() else this,
    if (ignoreCase) other.lowercase() else other
)

fun String.meanDistance(strings: List<String>): Double = strings.sumOf { it.distance(this) }.toDouble() / strings.size
fun String.minDistance(selection: List<String>, ignoreCase: Boolean = true): Int = selection.minOf { it.distance(this, ignoreCase) }

fun String.isSimilar(other: String, ignoreCase: Boolean = true): Boolean = distance(other) <= minOf(6, this.length / 4, other.length / 4)
fun String.isSimilar(selection: List<String>, ignoreCase: Boolean = true): Boolean = minDistance(selection) <= minOf(this.length / 4, 6, minCase(selection).length / 4)