package com.example.ticktapp.util
import android.text.method.PasswordTransformationMethod
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.example.ticktapp.R



@BindingAdapter("changePasswordToggle")
fun changePasswordToggle(imageView: AppCompatImageView, temp: Int?) {
    imageView.setOnClickListener {
//        val imageView = it as AppCompatImageView
        val toggleValue = (imageView.tag as? Boolean) ?: false
        imageView.parent?.let { viewParent ->
            val viewGroup = viewParent as ViewGroup
            if (viewGroup.childCount > 0)
                if (viewGroup.getChildAt(0) is AppCompatEditText) {
                    val editText = viewGroup.getChildAt(0) as AppCompatEditText
                    if (toggleValue) {
                        editText.transformationMethod = PasswordTransformationMethod()
                        if(temp==null)
                        imageView.setImageResource(R.drawable.icon_eye_closed)
                        else
                            imageView.setImageResource(R.drawable.ic_icon_eye_closed_dark)
                    } else {
                        editText.transformationMethod = null
                        if(temp==null)
                        imageView.setImageResource(R.drawable.visibility_1)
                        else
                            imageView.setImageResource(R.drawable.visibility_2)
                    }
                    editText.setSelection(editText.text.toString().length)
                    imageView.tag = !toggleValue
                }
        }
    }


    @BindingAdapter("changePasswordDarkToggle")
    fun changePasswordToggleDark(imageView: AppCompatImageView, temp: Int?) {
        imageView.setOnClickListener {
//        val imageView = it as AppCompatImageView
            val toggleValue = (imageView.tag as? Boolean) ?: false
            imageView.parent?.let { viewParent ->
                val viewGroup = viewParent as ViewGroup
                if (viewGroup.childCount > 0)
                    if (viewGroup.getChildAt(0) is AppCompatEditText) {
                        val editText = viewGroup.getChildAt(0) as AppCompatEditText
                        if (toggleValue) {
                            editText.transformationMethod = PasswordTransformationMethod()
                            imageView.setImageResource(R.drawable.ic_icon_eye_closed_dark)
                        } else {
                            editText.transformationMethod = null
                            imageView.setImageResource(R.drawable.visibility_2)
                        }
                        editText.setSelection(editText.text.toString().length)
                        imageView.tag = !toggleValue
                    }
            }
        }

    }

}



