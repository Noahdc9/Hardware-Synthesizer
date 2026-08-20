import chisel3._
import chisel3.util._

class I2SDriver extends Module{
    val width = 32
    val io = IO(new Bundle{
        //inputs
         val BCLKInput = Input(Bool())

        val audioInputL = Input(SInt(width.W))
        val audioInputR = Input(SInt(width.W))
        
        //outputs
        val BCLKOutput = Output(Bool())
        val DIN = Output(Bool())
        val LRC = Output(Bool())
        val sampleReady = Output(Bool())
        
    })
    

    val clkReg = RegInit(false.B)
    when(io.bclkTick) { clkReg := !clkReg }
    val risingEdge = io.BCLKInput && !clkReg
    io.BCLKOutput := clkReg




    val audioShiftRegL = RegInit(0.S(width.W))
    val audioShiftRegR = RegInit(0.S(width.W))

    val bit_counter = RegInit(0.U(6.W))
    val sampleReadyReg = RegInit(false.B)
    val LRCReg = RegInit(false.B)
    val DINReg = RegInit(false.B)
    //signal to audio generator that we want new audio.
    //only high when bit_counter is 63 (we are done with the data)
    io.sampleReady := sampleReadyReg
    io.LRC := LRCReg
    io.DIN := DINReg

    DINReg := Mux(LRCReg,audioShiftRegR(width-1),audioShiftRegL(width-1)).asUInt

    when(risingEdge){
        bit_counter := bit_counter + 1.U
        //driving of bit counter, LRC choice
        when(bit_counter <= 31.U && 0.U <= bit_counter){
            LRCReg := false.B
        }.elsewhen(bit_counter <= 63.U && 32.U <= bit_counter){
            LRCReg := true.B
        }
        //when done with data, send sampleReady to AudioGenerator. otherwise, keep at 0.
        when(bit_counter === 63.U){
            sampleReadyReg := true.B
        }.otherwise{
            sampleReadyReg := false.B
        }



        //driving of shift register and DIN.
        when(bit_counter === 0.U){
            audioShiftRegL := io.audioInputL
            audioShiftRegR := io.audioInputR

        }.otherwise{
            when(bit_counter < width.U){
                audioShiftRegL := audioShiftRegL << 1

            }.otherwise{
                audioShiftRegR := audioShiftRegR << 1
            }

        }



    }
}