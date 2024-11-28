package com.superbgoal.caritasrig.data.model.buildmanager

import android.os.Parcelable
import com.superbgoal.caritasrig.data.model.component.Casing
import com.superbgoal.caritasrig.data.model.component.CpuCooler
import com.superbgoal.caritasrig.data.model.component.GpuBuild
import com.superbgoal.caritasrig.data.model.component.Headphones
import com.superbgoal.caritasrig.data.model.component.InternalHardDrive
import com.superbgoal.caritasrig.data.model.component.Keyboard
import com.superbgoal.caritasrig.data.model.component.Memory
import com.superbgoal.caritasrig.data.model.component.Motherboard
import com.superbgoal.caritasrig.data.model.component.Mouse
import com.superbgoal.caritasrig.data.model.component.PowerSupply
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.ProcessorTrial
import com.superbgoal.caritasrig.data.model.component.VideoCard
import kotlinx.parcelize.Parcelize

@Parcelize
data class BuildComponents(
    val casing: Casing? = null,
    val cpuCooler: CpuCooler? = null,
    val motherboard: Motherboard? = null,
    val processor: ProcessorTrial? = null,
    val memory: Memory? = null,
    val internalHardDrive: InternalHardDrive? = null,
    val powerSupply: PowerSupply? = null,
    val keyboard: Keyboard? = null,
    val mouse: Mouse? = null,
    val videoCard: GpuBuild? = null,
    val headphone: Headphones? = null
) : Parcelable
