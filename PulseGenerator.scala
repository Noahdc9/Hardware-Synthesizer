class PulseGenerator(divideBy: Int) extends Module {
  val io = IO(new Bundle {
    val pulse = Output(Bool())
  })

  val counterReg = RegInit(0.U(log2Ceil(divideBy).W))
  val pulseReg   = RegInit(false.B)

  pulseReg := false.B
  when(counterReg === (divideBy - 1).U) {
    counterReg := 0.U
    pulseReg   := true.B
  }.otherwise {
    counterReg := counterReg + 1.U
  }

  io.pulse := pulseReg
}