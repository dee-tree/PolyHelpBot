package com.techproj.polyhelpbot.locations

import com.techproj.polyhelpbot.Keyword
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

internal object PhysicalPlaceKeywordsTable : IdTable<Keyword>() {
    val keyword = varchar("keyword", 50).uniqueIndex()
    override val id: Column<EntityID<Keyword>> = varchar("id", 50).entityId()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}


internal class PhysicalPlaceKeywordDAO(id: EntityID<Keyword>) : Entity<Keyword>(id) {
    companion object : EntityClass<Keyword, PhysicalPlaceKeywordDAO>(PhysicalPlaceKeywordsTable) {
        fun Keyword.newDAO(): PhysicalPlaceKeywordDAO = PhysicalPlaceKeywordDAO.new(this) {
            keyword = this@newDAO
        }

        @JvmName("newDAO1")
        fun newDAO(keyword: Keyword): PhysicalPlaceKeywordDAO = PhysicalPlaceKeywordDAO.new(keyword) {
            this.keyword = keyword
        }
    }

    var keyword by PhysicalPlaceKeywordsTable.keyword

    fun toModel(): Keyword = keyword
}