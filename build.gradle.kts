import toni.blahaj.*
import toni.blahaj.api.*

val templateSettings = object : BlahajSettings {
	// -------------------- Dependencies ---------------------- //
	override val depsHandler: BlahajDependencyHandler get() = object : BlahajDependencyHandler {
		override fun addGlobal(mod : ModData, deps: DependencyHandler) {

		}

		override fun addFabric(mod : ModData, deps: DependencyHandler) {
			when (mod.mcVersion) {
				"1.21.4" -> {
					deps.modImplementation(modrinth("sodium", "mc1.21.4-0.6.6-fabric"))
					deps.modImplementation(modrinth("reeses-sodium-options", "mc1.21.4-1.8.2+fabric"))

					deps.runtimeOnly("me.fallenbreath:conditional-mixin-fabric:0.6.3")
					deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0-beta.6")!!)!!)
				}
				"1.21.1" -> {
					deps.modImplementation(modrinth("sodium", "mc1.21.1-0.6.5-fabric"))
					deps.modRuntimeOnly(modrinth("moreculling", "UncAG2fS"))
					deps.modRuntimeOnly(modrinth("cloth-config", "15.0.140+fabric"))
					deps.runtimeOnly("me.fallenbreath:conditional-mixin-fabric:0.6.3")
					deps.modImplementation(modrinth("reeses-sodium-options", "mc1.21.3-1.8.0+fabric"))

					deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0-beta.6")!!)!!)

				}
				"1.20.1" -> {
					deps.modImplementation(modrinth("sodium", "mc1.20.1-0.5.11"))
					deps.modImplementation(modrinth("reeses-sodium-options", "mc1.20.1-1.7.2"))
					deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0-beta.6")!!)!!)
				}
			}
		}

		override fun addForge(mod : ModData, deps: DependencyHandler) {
			deps.modImplementation(modrinth("embeddium", "0.3.31+mc1.20.1"))
			deps.include(deps.implementation("dev.su5ed.sinytra.fabric-api:fabric-api-base:0.4.31+ef105b4977")!!)
			deps.compileOnly(deps.annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
			deps.implementation(deps.include("io.github.llamalad7:mixinextras-forge:0.4.1")!!)
			deps.modRuntimeOnly(modrinth("oculus", "1.20.1-1.7.0"))
			deps.runtimeOnly("org.anarres:jcpp:1.4.14")
		}

		override fun addNeo(mod : ModData, deps: DependencyHandler) {
			deps.compileOnly(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.2.0")!!)
			deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-neoforge:0.2.0")!!)!!)

			when (mod.mcVersion)
			{
				"1.21.4" -> {
					deps.implementation(modrinth("sodium", "mc1.21.4-0.6.6-neoforge"))
					deps.implementation(modrinth("reeses-sodium-options", "mc1.21.4-1.8.2+neoforge"))

					deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308ded19")
					deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-renderer-api-v1:5.0.0+babc52e504")
					deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-rendering-data-attachment-v1:0.3.48+73761d2e19")
					deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-block-view-api-v2:1.0.10+9afaaf8c19")
				}
				"1.21.1" -> {
					deps.implementation(modrinth("sodium", "mc1.21.1-0.6.0-neoforge"))
					deps.implementation(modrinth("reeses-sodium-options", "mc1.21.3-1.8.0+neoforge"))

					deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308dedd1")
					deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-renderer-api-v1:3.4.0+acb05a39d1")

					deps.runtimeOnly(modrinth("sodium-dynamic-lights", "z57UcDuv"))

				}
			}
		}
	}

	// ---------- Curseforge/Modrinth Configuration ----------- //
	// For configuring the dependecies that will show up on your mod page.
	override val publishHandler: BlahajPublishDependencyHandler get() = object : BlahajPublishDependencyHandler {
		override fun addShared(mod : ModData, deps: DependencyContainer) {
			if (mod.isFabric) {
				deps.requires("fabric-api")
			}

			if (mod.isForge)
				deps.requires("embeddium")
			else
				deps.requires("sodium")
		}

		override fun addCurseForge(mod : ModData, deps: DependencyContainer) {

		}

		override fun addModrinth(mod : ModData, deps: DependencyContainer) {

		}
	}
}

plugins {
	`maven-publish`
	application
	id("toni.blahaj") version "1.0.15"
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("dev.kikugie.j52j") version "1.0"
	id("dev.architectury.loom")
	id("me.modmuss50.mod-publish-plugin")
	id("systems.manifold.manifold-gradle-plugin")
}

blahaj {
	sc = stonecutter
	settings = templateSettings
	init()
}

// Dependencies
repositories {
	maven("https://maven.pkg.github.com/ims212/ForgifiedFabricAPI") {
		credentials {
			username = "IMS212"
			// Read only token
			password = "ghp_" + "DEuGv0Z56vnSOYKLCXdsS9svK4nb9K39C1Hn"
		}
	}
	maven("https://www.cursemaven.com")
	maven("https://api.modrinth.com/maven")
	maven("https://thedarkcolour.github.io/KotlinForForge/")
	maven("https://maven.kikugie.dev/releases")
	maven("https://maven.txni.dev/releases")
	maven("https://jitpack.io")
	maven("https://maven.neoforged.net/releases/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
	maven("https://maven.parchmentmc.org")
	maven("https://maven.su5ed.dev/releases")
	maven("https://maven.su5ed.dev/releases")
	maven("https://maven.fabricmc.net")
	maven("https://maven.shedaniel.me/")
	maven("https://maven.fallenbreath.me/releases")
}
