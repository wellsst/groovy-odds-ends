package text


def text = "one two 1 2 hhh h1x h1. h2. p<>. fn1. fn1. p(. {background:#ddd}. "
def matcher = /\w+\b(?<!\b[fn\d\.]|[p{1,30}\.]|[h\d\.]|[\{.{1,20}:#.{3}\}\.])/

def check = text =~ matcher

println check.count
