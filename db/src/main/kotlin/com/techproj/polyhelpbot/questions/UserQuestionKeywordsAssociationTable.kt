package com.techproj.polyhelpbot.questions

import org.jetbrains.exposed.sql.Table

/**
 * For many-to-many association
 */
object UserQuestionKeywordsAssociationTable : Table() {
    val userQuestion = reference("userQuestion", UserQuestionsTable)
    val keyword = reference("keyword", UserQuestionsKeywordsTable)

    override val primaryKey: PrimaryKey = PrimaryKey(userQuestion, keyword, name="PK_UserQuestionsKeywordsAssociation_pp_kw")
}