plugins {
	id("toni.blahaj")
}

blahaj {
	config { }
	setup {
		if (mod.isForge) {
			markRequiredAll("embeddium")
		}
		else {
			markRequiredAll("sodium")
			modloaderRequired("sodium")
		}

		if (mod.isNeo) {
			deps.compileOnly(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.2.0-beta.6")!!)
			deps.include(deps.implementation("com.github.bawnorton.mixinsquared:mixinsquared-neoforge:0.2.0-beta.6")!!)!!
		}

		when (mod.projectName) {
			"1.20.1-fabric" -> {
				deps.modImplementation(modrinth("sodium", "mc1.20.1-0.5.11"))
				deps.modImplementation(modrinth("reeses-sodium-options", "mc1.20.1-1.7.2"))
				deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0-beta.6")!!)!!)
			}

			"1.20.1-forge" -> {
				deps.modImplementation(modrinth("embeddium", "0.3.31+mc1.20.1"))
				deps.include(deps.implementation("dev.su5ed.sinytra.fabric-api:fabric-api-base:0.4.31+ef105b4977")!!)
				deps.compileOnly(deps.annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
				deps.implementation(deps.include("io.github.llamalad7:mixinextras-forge:0.4.1")!!)
				deps.modRuntimeOnly(modrinth("oculus", "1.20.1-1.7.0"))
				deps.runtimeOnly("org.anarres:jcpp:1.4.14")
			}

			"1.21.1-fabric" -> {
				deps.modImplementation(modrinth("sodium", "mc1.21.1-0.6.13-fabric"))
				deps.modRuntimeOnly(modrinth("moreculling", "UncAG2fS"))
				deps.modRuntimeOnly(modrinth("cloth-config", "15.0.140+fabric"))
				deps.runtimeOnly("me.fallenbreath:conditional-mixin-fabric:0.6.3")
				deps.modImplementation(modrinth("reeses-sodium-options", "mc1.21.4-1.8.3+fabric"))
				deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0-beta.6")!!)!!)
			}

			"1.21.1-neoforge" -> {
				deps.implementation(modrinth("sodium", "mc1.21.1-0.6.13-neoforge"))
				deps.implementation(modrinth("reeses-sodium-options", "mc1.21.3-1.8.0+neoforge"))

				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308dedd1")
				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-renderer-api-v1:3.4.0+acb05a39d1")

				deps.runtimeOnly(modrinth("sodium-dynamic-lights", "z57UcDuv"))
			}

			"1.21.4-fabric" -> {
				deps.modImplementation(modrinth("sodium", "mc1.21.4-0.6.13-fabric"))
				deps.modImplementation(modrinth("reeses-sodium-options", "mc1.21.4-1.8.3+fabric"))
				deps.runtimeOnly("me.fallenbreath:conditional-mixin-fabric:0.6.3")
				deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0-beta.6")!!)!!)
			}

			"1.21.4-neoforge" -> {
				deps.implementation(modrinth("sodium", "mc1.21.4-0.6.13-neoforge"))
				deps.implementation(modrinth("reeses-sodium-options", "mc1.21.4-1.8.3+neoforge"))

				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308ded19")
				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-renderer-api-v1:5.0.0+babc52e504")
				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-rendering-data-attachment-v1:0.3.48+73761d2e19")
				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-block-view-api-v2:1.0.10+9afaaf8c19")
			}

			"1.21.5-fabric" -> {
				deps.modImplementation(modrinth("sodium", "mc1.21.5-0.6.13-fabric"))
				deps.modImplementation(modrinth("reeses-sodium-options", "mc1.21.4-1.8.3+fabric"))
				deps.runtimeOnly("me.fallenbreath:conditional-mixin-fabric:0.6.3")
				deps.include(deps.implementation(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.2.0-beta.6")!!)!!)
			}

			"1.21.5-neoforge" -> {
				deps.implementation(modrinth("sodium", "mc1.21.5-0.6.13-neoforge"))
				deps.implementation(modrinth("reeses-sodium-options", "mc1.21.4-1.8.3+neoforge"))

				deps.compileOnly(deps.annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:0.2.0-beta.6")!!)
				deps.include(deps.implementation("com.github.bawnorton.mixinsquared:mixinsquared-neoforge:0.2.0-beta.6")!!)!!

				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-api-base:0.4.42+d1308ded19")
				deps.compileOnly("net.caffeinemc:fabric-renderer-api-v1:6.0.0")
				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-rendering-data-attachment-v1:0.3.48+73761d2e19")
				deps.compileOnly("org.sinytra.forgified-fabric-api:fabric-block-view-api-v2:1.0.10+9afaaf8c19")
			}
		}
	}
}