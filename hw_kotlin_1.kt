fun List<Int>.sumOddCubes():Int {
    return this.filter{it % 2 != 0}.map{it*it*it}.sum()
}
