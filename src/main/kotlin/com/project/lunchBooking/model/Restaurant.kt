package com.project.lunchBooking.model

import javax.persistence.*

@Entity
data class Restaurant(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = -1,

    @Column
    var name: String

)