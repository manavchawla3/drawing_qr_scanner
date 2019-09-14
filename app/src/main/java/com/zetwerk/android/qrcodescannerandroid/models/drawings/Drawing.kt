package com.zetwerk.android.qrcodescannerandroid.models.drawings

import com.zetwerk.android.qrcodescannerandroid.models.attachment.Attachment


class Drawing {
    var drawing_no: String? = null
    var qty: String? = null
    var unit: String? = null
    var received_on: String? = null
    var processed_drawing: Attachment? = null
    var unprocessed_drawing: Attachment? = null


    override fun toString(): String {
        return "Drawing(drawing_no=$drawing_no, qty=$qty, unit=$unit, received_on=$received_on, processed_drawing=$processed_drawing, unprocessed_drawing=$unprocessed_drawing)"
    }


}
