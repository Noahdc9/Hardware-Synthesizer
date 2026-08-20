import chisel3._
import chisel3.util._

class AudioGenerator extends Module{
    val io = IO(new Bundle{
        //inputs
        //audio signals in
        //I2S input from driver
        val sampleReady = Input(Bool())
        //Outputs
        //
        val audioDataOut = Output(SInt(16.W))

     })


//REGISTERS

    //conditional registers

// outer condition for audio; if true, noteSelector is 0.



//source is our inner condition for noteSelector, and it is decided from inputs from the other modules.
val source = RegInit(0.U(4.W))
//source is 1.U at beerSlding, 2.U at bad throw, 3.U at ptScoring, 4.U at beer breaks, 5.U at game over.


        //AUDIO

    //halftime counter.
    val tonePeriodCountReg = RegInit(0.U(8.W))
    //note selector, selecting in tonePeriodLUT.
    val noteSelector = RegInit(0.U(4.W))
    //LUT of notes, calculated for 44.1khz sample rate.
    //(approximations)
    val tonePeriodLUT = MuxLookup(noteSelector, 0.U(12.W))(Seq(
  0.U  -> 0.U,    // Mute / Rest
  1.U  -> 175.U,   // Middle C  (261.6  Hz)
  2.U  -> 150.U,   // D4        (293.7  Hz)
  3.U  -> 133.U,   // E4        (329.6  Hz)
  4.U  -> 112.U,   // G4        (392.0  Hz)
  5.U  -> 100.U,    // A4       (440.0  Hz)
  6.U  -> 68.U,     // E5 (ish) (650.0  Hz)
  7.U  -> 50.U,     // A6       (880.0  Hz) 
  8.U  -> 42.U      // C6       (1050.0 Hz)
))
    

//the data to send. ~2^16.
val data = RegInit(32000.S(16.W))

    //INPUT SIGNALS

    //STUTTER
        //get a stuttery / "counting" audio effect by making a stutter condition.
        //an outer condition used both in ptscoring and in game over.

    val stutter = RegInit(false.B)
    //stutterCntReg is the counter for how long the stutter should be.
    val stutterCntReg = RegInit(0.U(12.W))
    //repeatCntReg to check how many stutters we make.
    val repeatCntReg = RegInit(0.U(7.W))




    when(stutterCntReg === 63.U){
        stutterCntReg := 0.U
        stutter := !stutter
        repeatCntReg := Mux(noteSelector === 5.U, repeatCntReg + 1.U, 0.U)

    }

    //sending of sample.
    when(io.sampleReady){
        //increment stutterreg when sending audio, if stutterReg should increment.
        when(source =/= 0.U && source =/= 1.U){
        stutterCntReg := stutterCntReg + 1.U

        }
        tonePeriodCountReg := tonePeriodCountReg + 1.U
        //flip the data! to make the squarewave actually be a squarewave.
        when(tonePeriodCountReg === tonePeriodLUT - 1.U){
            data := -data
            tonePeriodCountReg := 0.U
        }
    }










}