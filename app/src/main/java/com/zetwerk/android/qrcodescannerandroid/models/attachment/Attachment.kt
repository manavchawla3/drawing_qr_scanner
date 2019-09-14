package com.zetwerk.android.qrcodescannerandroid.models.attachment


class Attachment {
    var file_id: String? = null
    var file_url: String? = null

    override fun toString(): String {
        return "Attachment(file_id=$file_id, file_url=$file_url)"
    }

}
