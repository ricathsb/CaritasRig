package com.superbgoal.caritasrig.data.model.buildmanager

import android.os.Parcelable
import com.superbgoal.caritasrig.data.model.component.CasingBuild
import com.superbgoal.caritasrig.data.model.component.CpuCoolerBuild
import com.superbgoal.caritasrig.data.model.component.GpuBuild
import com.superbgoal.caritasrig.data.model.component.Headphones
import com.superbgoal.caritasrig.data.model.component.InternalHardDriveBuild
import com.superbgoal.caritasrig.data.model.component.Keyboard
import com.superbgoal.caritasrig.data.model.component.MemoryBuild
import com.superbgoal.caritasrig.data.model.component.MotherboardBuild
import com.superbgoal.caritasrig.data.model.component.Mouse
import com.superbgoal.caritasrig.data.model.component.PowerSupplyBuild
import com.superbgoal.caritasrig.data.model.component.ProcessorTrial
import kotlinx.parcelize.Parcelize

@Parcelize
data class BuildComponents(
    val casing: CasingBuild? = null,
    val cpuCooler: CpuCoolerBuild? = null,
    val motherboard: MotherboardBuild? = null,
    val processor: ProcessorTrial? = null,
    val memory: MemoryBuild? = null,
    val internalHardDrive: InternalHardDriveBuild? = null,
    val powerSupply: PowerSupplyBuild? = null,
    val keyboard: Keyboard? = null,
    val mouse: Mouse? = null,
    val videoCard: GpuBuild? = null,
    val headphone: Headphones? = null
) : Parcelable
