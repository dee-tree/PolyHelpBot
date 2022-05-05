package com.techproj.polyhelpbot

import org.apache.commons.text.similarity.LevenshteinDistance

private val dist = LevenshteinDistance.getDefaultInstance()


fun String.minDistance(variants: List<String>): Int {
    return variants.minOf { distance(it) }
}

fun String.distance(other: String, ignoreCase: Boolean = true): Int = dist.apply(
    if (ignoreCase) this.lowercase() else this,
    if (ignoreCase) other.lowercase() else other
)

fun String.meanDistance(strings: List<String>): Double = strings.sumOf { it.distance(this) }.toDouble() / strings.size
fun String.minDistance(selection: List<String>, ignoreCase: Boolean = true): Int = selection.minOf { it.distance(this, ignoreCase) }

fun String.isSimilar(other: String, ignoreCase: Boolean = true): Boolean = distance(other) < 2
fun String.isSimilar(selection: List<String>, ignoreCase: Boolean = true): Boolean = minDistance(selection) < 2