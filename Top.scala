//////////////////////////////////////////////////////////////////////////////
// Authors: Noah Dahl Christensen
// Copyright: me:)
// Comments:
//
//////////////////////////////////////////////////////////////////////////////

import chisel3._
import chisel3.util._

class Top extends Module {
  val io = IO(new Bundle {
    //Buttons
    val btnC = Input(Bool())
    val btnU = Input(Bool())
    val btnL = Input(Bool())
    val btnR = Input(Bool())
    val btnD = Input(Bool())


    //Switches
    val switches = Input(Vec(16, Bool()))



  })



//VALUES
//switches:

//let 8 leftmost switches be notes in a chord.
val notes = RegInit(VecInit.fill(8)(0.U(1.W)))
//let all 16 be sequencer notes.
val sequencer = RegInit(VecInit.fill(16)(0.U(1.W)))


val PulseGen = Module(new(PulseGenerator(16)))
val I2SDriver = Module(new(I2SDriver()))

I2SDriver.io.BCLKInput := PulseGen.io.pulse

}

////////////////////////////////////////////////////////////
// An object extending App to generate the Verilog code.
////////////////////////////////////////////////////////////
object Top extends App {
  println("Processing Top, I will now generate the Verilog file!")
  emitVerilog(new Top())
  println("#####################################################")
  println("#########SUCCESFULLY GENERATED VERILOG FILE##########")
  println("#####################################################")
}

//////////////////////////////////////////////////////////////////////////////
// End of file
//////////////////////////////////////////////////////////////////////////////
