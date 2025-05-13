import z from "zod";

const Identifier = z.number().positive();
const Student = z.object({
    id: z.string(),
    name: z.string(),
    status: z.enum(['ACTIVE', 'INACTIVE'])
 });

 const Course = z.object({
     id: z.string(),
     name: z.string(),
     description: z.string(),
});

const Enrollment = z.object({
    student: Student,
    course: Course,
    })