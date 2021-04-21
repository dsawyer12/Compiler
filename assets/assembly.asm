sys_exit	equ	1
sys_read	equ	3
sys_write	equ	4
stdin		equ	0
stdout		equ	1
stderr		equ	3

section .data	
	userMsg			db	'Enter an integer(less than 32,765): '
	lenUserMsg		equ	$-userMsg
	displayMsg		db	'You entered: '
	lenDisplayMsg	equ	$-displayMsg
	newline			db	0xA

	Ten				DW	10

	Result			db	'Ans = '
	ResultValue		db	'aaaaa'
					Db	0xA
	ResultEnd		equ	$-Result   

	num	times	6	db	'ABCDEF'
	numEnd			equ	$-num

	M		DW	13	
	N		DW	56	
	LIT97		DW	97	
	LIT2		DW	2	
	LIT18		DW	18	
section	.bss
	TempChar	RESB	1	
	testchar	RESB	1	
	ReadInt		RESW	1
	tempint		RESW	1
	negflag		RESB	1

	X			RESW	1
	Y			RESW	1
	Z			RESW	1
	T1			RESW	1
	T2			RESW	1
	T3			RESW	1
section	.text	
	global main 
main:
	again: call PrintString
	call GetAnInteger

	mov ax, [ReadInt]
	mov [Y], ax

	mov ax, [LIT97]
	mov [Z], ax

	mov dx, 0
	mov ax, [M]
	mov bx, [LIT2]
	div bx
	mov [T1], ax

	mov ax, [T1]
	add ax, [LIT18]
	mov [T1], ax

	mov ax, [T1]
	sub ax, [Y]
	mov [T1], ax

	mov ax, [T1]
	mov [X], ax

	mov ax, [M]
	cmp ax, [Y]
	jge L1

	mov ax, [X]
	mov [Y], ax

	L1:	nop

	call PrintString

