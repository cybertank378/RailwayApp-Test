package com.rahman.railwayapp.core.util.validator

object InputValidator {

    fun validateInput(title: String): StringValidation =
        when {
            title.isBlank() ->
                StringValidation(false, "Title is required")

            title.length > 255 ->
                StringValidation(false, "Title max 255 characters")

            else ->
                StringValidation(true)
        }

    fun validateDescription(description: String): StringValidation =
        if (description.length > 1000)
            StringValidation(false, "Description max 1000 characters")
        else
            StringValidation(true)
}