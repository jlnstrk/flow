package de.julianostarek.flow.profile.util

val PATTERN_SPLIT_PLACE_COMMA_NAME = Regex("([^,]+), (.+)")
val PATTERN_SPLIT_NAME_COMMA_PLACE = Regex("(.*), ([^,]*)")
val PATTERN_SPLIT_NAME_NEXT_TO_LAST_COMMA = Regex("(.*), ([^,]*, [^,]*)")
val PATTERN_PLACE_IN_PARENTHESES = Regex("(.*) \\((.{3,}?)\\)")

val PATTERN_BERLIN_NAME_PLACE_SUFFIX = Regex("(.*(?<! )) ?\\((.+?)\\) ?(?:\\[(.*)])?")
val PATTERN_BERLIN_NAME_SUFFIX = Regex("(.*(?<! )) ?(?:\\[(.*)])")