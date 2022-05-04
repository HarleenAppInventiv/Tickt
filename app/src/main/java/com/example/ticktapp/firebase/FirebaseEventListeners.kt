package com.example.ticktapp.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

open class FirebaseEventListeners : ChildEventListener, ValueEventListener {
    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {}
    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
    override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
    override fun onDataChange(dataSnapshot: DataSnapshot) {}
    override fun onCancelled(databaseError: DatabaseError) {}
}