pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.architectury.dev")
		maven("https://maven.minecraftforge.net")
		maven("https://maven.neoforged.net/releases/")
		maven("https://maven.kikugie.dev/snapshots")
		maven("https://maven.kikugie.dev/releases")
		maven("https://maven.txni.dev/releases")
	}
}

plugins {
	id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
	kotlin("jvm") version "2.0.0" apply false
	kotlin("plugin.serialization") version "2.0.0" apply false
	id("toni.blahaj") version "2.0.15"
	id("dev.kikugie.stonecutter") version "0.6-alpha.5"
}

blahaj {
	init(rootProject) {
		//mc("1.20.1", "fabric", "forge")
		//mc("1.21.1", "fabric", "neoforge")
		//mc("1.21.4", "fabric", "neoforge")
		mc("1.21.5", "fabric", "neoforge")
	}
}

rootProject.name = settings.extra["mod.name"] as String