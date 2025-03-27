abstract class AnimalBase(var overseer: Overseer, val id: Int, val height: Int)
{

}

interface Soundable {
    fun sound():String
}

class Cat(overseer: Overseer, id: Int, height: Int): AnimalBase(overseer, id, height), Soundable {
    override fun sound(): String = "MEOW MURMURMUR"
}
class Dog(overseer: Overseer, id: Int, height: Int): AnimalBase(overseer, id, height), Soundable {
    override fun sound():String = "FENRŸS HJÖLDA"
}
class Hippo(overseer: Overseer, id: Int, height: Int): AnimalBase(overseer, id, height), Soundable {
    override fun sound(): String = "UAAHH"
}
class Horse(overseer: Overseer, id: Int, height: Int): AnimalBase(overseer, id, height), Soundable {
    override fun sound(): String = "IGOGO"
}
class Fish(overseer: Overseer, id: Int, height: Int): AnimalBase(overseer, id, height) {

}

data class Overseer(val name: String, val id:Int)
{

}
class Zoo
{
    private val animals: MutableMap<Int, AnimalBase>
    private val overseers_track: MutableMap<Int, Pair<Overseer, MutableSet<Int>>>
    private val overseers_by_names: MutableMap<String, MutableSet<Int>>
    constructor(anims: MutableSet<AnimalBase>)
    {
        this.animals = anims.associateBy { it.id }.toMutableMap()
        this.overseers_track = anims.map{ it.overseer.id to (it.overseer to mutableSetOf(it.id)) }.toMap().toMutableMap()
        this.overseers_by_names = overseers_track.map {(k, v) -> v.first.name to mutableSetOf(k)}.toMap().toMutableMap()
    }
    constructor()
    {
        this.animals = mutableMapOf()
        this.overseers_track = mutableMapOf()
        this.overseers_by_names = mutableMapOf()
    }
    private fun addAnimalToOverseer(overseer_id: Int, animal: Int, overseer: Overseer)
    {
        if (this.overseers_track[overseer_id] == null) {
            overseers_track.put(overseer_id , (overseer to mutableSetOf(animal)))
            if (this.overseers_by_names[overseer.name] == null)
                overseers_by_names.put(overseer.name, mutableSetOf(overseer.id))
            else
                overseers_by_names[overseer.name]!!.add(overseer.id)
        }
        else
        {
            overseers_track[overseer_id]!!.second.add(animal)
        }
    }
    fun addAnimal(animal: AnimalBase)
    {
        this.animals.put(animal.id, animal)
        addAnimalToOverseer(animal.overseer.id, animal.id, animal.overseer)

    }
    fun findAnimal(id: Int): AnimalBase?
    {
        return animals[id]
    }
    private fun removeAnimalFromOverseer(overseer: Int, animal: Int)
    {
        overseers_track[overseer]!!.let {
            it.second.remove(animal)
            if (it.second.isEmpty()) {
                if (this.overseers_by_names[it.first.name]!!.size == 1)
                    this.overseers_by_names.remove(it.first.name)
                else
                    this.overseers_by_names[it.first.name]!!.remove(overseer)
                this.overseers_track.remove(it.first.id)
            }
        }
    }
    fun deleteAnimal(id: Int)
    {
        removeAnimalFromOverseer(animals[id]?.overseer?.id ?: return, id)
        animals.remove(id)

    }
    fun assignOverseer(animal: AnimalBase, overseer: Overseer)
    {
        assignOverseer(animal.id, overseer)
    }
    fun assignOverseer(id: Int, overseer: Overseer)
    {
        removeAnimalFromOverseer(animals[id]?.overseer?.id ?: return, id)
        animals[id]?.let{it.overseer = overseer} ?: return
        addAnimalToOverseer(overseer.id, id, overseer)
    }
    fun animalsOfOverseer(overseers_id: Int): List<AnimalBase> =
        this.overseers_track[overseers_id]?.second?.map { animals[it]!! } ?: listOf()
    fun animalsOfOverseer(name: String): List<AnimalBase> =
        this.overseers_by_names[name]?.map{animalsOfOverseer(it)}?.flatten()  ?: listOf()

    fun animalsOfHeightMoreThan(height: Int): List<AnimalBase> = this.animals.filterValues { it.height > height }.values.toList()
    fun soundableAnimals() : List<Soundable> = this.animals.values.filterIsInstance<Soundable>().toList()
    inline fun <reified T> filterByType(): List<T> = this.getAnimals().values.filterIsInstance<T>().toList()

    @PublishedApi
    internal fun getAnimals(): Map<Int, AnimalBase> = animals

    fun printZoo() {
        println("\nPrinting zoo---------")
        animals.forEach { println("Animal ${it.key}  Overseered by (${it.value.overseer.name}, ${it.value.overseer.id})") }
    }
}
