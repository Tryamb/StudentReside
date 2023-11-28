package com.tryamb.studentReside.study

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tryamb.studentReside.R
import com.tryamb.studentReside.study.fragments.UploadStudyFragment
import com.tryamb.studentReside.study.fragments.ViewStudyFragment


class StudyZone : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_zone)
        loadFragment(ViewStudyFragment())

        findViewById<Button>(R.id.btnAddContent).setOnClickListener {
            loadFragment(UploadStudyFragment())
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
}