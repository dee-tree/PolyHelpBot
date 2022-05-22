package com.techproj.polyhelpbot.questions

import com.techproj.polyhelpbot.Keyword
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

internal object UserQuestionsKeywordsTable : IdTable<Keyword>() {
    val keyword = varchar("keyword", 255).uniqueIndex()

    override val id: Column<EntityID<Keyword>> = varchar("id", 255).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}


internal class UserQuestionKeywordDAO(id: EntityID<Keyword>) : Entity<Keyword>(id) {
    companion object : EntityClass<Keyword, UserQuestionKeywordDAO>(UserQuestionsKeywordsTable) {
        fun Keyword.newDAO(): UserQuestionKeywordDAO = UserQuestionKeywordDAO.new(this) {
            keyword = this@newDAO
        }

        @JvmName("newDAO1")
        fun newDAO(keyword: Keyword): UserQuestionKeywordDAO = UserQuestionKeywordDAO.new(keyword) {
            this.keyword = keyword
        }
    }

    var keyword by UserQuestionsKeywordsTable.keyword

    fun toModel(): Keyword = keyword
}