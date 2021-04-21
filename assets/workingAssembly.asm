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

	M		DW	7
	N		DW	85
	LIT3		DW	3
	LIT12		DW	12
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

	mov ax, [LIT3]
	mov [Z], ax

	mov ax, [Y]
	mul WORD [Z]
	mov [T1], ax

	mov ax, [M]
	add ax, [T1]
	mov [T1], ax

	mov ax, [T1]
	add ax, [LIT12]
	mov [T1], ax

	mov ax, [T1]
	mov [X], ax

	mov ax, [X]
	call ConvertIntegerToString
	call PrintString

	mov eax, 4
	mov ebx, 1
	mov ecx, Result
	mov edx, ResultEnd
	int 80h

fini:
	mov eax, sys_exit
	xor ebx, ebx
	int 80h

PrintString:
	push    ax
	push    dx

	mov eax, 4
	mov ebx, 1
	mov ecx, userMsg
	mov edx, lenUserMsg
	int	80h
	pop     dx
	pop     ax
	ret

GetAnInteger:
	mov eax,3
	mov ebx,2
	mov ecx,num
	mov edx,6
	int 0x80

	mov edx,eax
	mov eax, 4
	mov ebx, 1
	mov ecx, num
	int 80h

ConvertStringToInteger:
	mov ax, 0
	mov [ReadInt], ax
	mov ecx, num

	mov bx, 0
	mov bl, byte [ecx]

Next:
    sub bl, '0'
	mov ax, [ReadInt]
	mov dx, 10
	mul dx
	add ax, bx
	mov [ReadInt],  ax

	mov bx, 0
	add ecx, 1
	mov bl, byte[ecx]

	cmp bl, 0xA
	jne Next
	ret

ConvertIntegerToString:
	mov ebx, ResultValue + 4

ConvertLoop:
	sub dx, dx
	mov cx, 10
	div cx
	add dl, '0'
	mov [ebx], dl
	dec ebx
	cmp ebx, ResultValue
	jge ConvertLoop
	ret