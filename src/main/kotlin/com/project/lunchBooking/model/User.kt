package com.project.lunchBooking.model

import javax.persistence.*

@Entity
data class User(
    // IDENTITY: generated by DB (increasing)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = -1,

    @Column
    var username: String,

    @Column
    var password: String,

    @Column
    var email: String,

    @Column
    var verify: Boolean

)
