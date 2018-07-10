<h1><a id="Turing_0"></a>Turing</h1>
<blockquote>
<p>A Non-Deterministic Multi-Tape Turing<br>
machine is like a Turing machine but it<br>
has several tapes and its set of rules<br>
may prescribe more than one action to<br>
be performed for any given situation.</p>
</blockquote>
<h3><a id="What_is_it_7"></a>What is it?</h3>
<p>It’s just another Turing machine simulator. It can handle non-deterministic events and there is no limit to tapes number.</p>
<h3><a id="Development_10"></a>Development</h3>
<p>It could be a good starting point for a more complete and complex Turing machine simulator.</p>
<p>If you find a bug or just want to contribute to the project, feel free to help or contact me by mail: <a href="mailto:mirkoalicastro@gmail.com">mirkoalicastro@gmail.com</a>. It would be very appreciate!</p>
<h3><a id="Syntax_15"></a>Syntax</h3>
<p>The first line of the program file must be the input (without starting symbol).<br>
Then, each line consists of:</p>
<pre><code>state; configuration; relation
</code></pre>
<p>Where,</p>
<ul>
<li><strong>state</strong> is a string which represents the name of the state;</li>
<li><strong>configuration</strong> is a sequence enclosed between two brackets where each elements is separated by a comma. The i-th element represents the character on the i-th tape;</li>
<li><strong>relation</strong> is a relation (not necessarily a function). It is represented by a sequence enclosed between two brackets where each elements is separated by a comma. The first element is the <em>state</em> where the Turing machine will go if it is in the state <em>state</em> and has the configuration <em>configuration</em>. Then, there are a two elements of each tape. The first one, for each tape, represents the character which will be written by the head of this tape. The second one, for each tape, represents the direction where the head of this tape will go, in particular:
<ul>
<li><strong>R</strong> for right;</li>
<li><strong>L</strong> for left;</li>
<li><strong>-</strong> don’t move;</li>
</ul>
</li>
</ul>
<h4><a id="Reserved_Character_29"></a>Reserved Characters</h4>
<table class="table table-striped table-bordered">
<thead>
<tr>
<th>Char</th>
<th>Meaning</th>
</tr>
</thead>
<tbody>
<tr>
<td>&gt;</td>
<td>Starting symbol</td>
</tr>
<tr>
<td>_</td>
<td>Blank symbol</td>
</tr>
<tr>
<td>s</td>
<td>Initial state</td>
</tr>
<tr>
<td>H</td>
<td>Halt: final state</td>
</tr>
<tr>
<td>Y</td>
<td>Yes: final state</td>
</tr>
<tr>
<td>N</td>
<td>No: final state</td>
</tr>
</tbody>
</table>
<h4><a id="An_example_39"></a>An example</h4>
<p>Check if a binary string is <u>not</u> a palindrome one.</p>
<pre><code>001100
s; (&gt;, &gt;); (s, &gt;, R, &gt;, R)
s; (1, _); (s, 1, R, _, -)
s; (1, _); (s, 1, R, 1, R)
s; (0, _); (s, 0, R, _, -)
s; (0, _); (s, 0, R, 1, R)
s; (_, _); (b, _, L, _, L)
b; (1,1); (b, 1, L, 1, L)
b; (0,1); (b, 0, L, 1, L)
b; (1, &gt;); (z1, 1, L, &gt;, R)
b; (0, &gt;); (z0, 0, L, &gt;, R)
b; (&gt;, &gt;); (N, &gt;, R, &gt;, R)
z0; (1, 1); (z0, 1, L, 1, -)
z0; (0, 1); (z0, 0, L, 1, -)
z0; (&gt;, 1); (q0, &gt;, R, 1, -)
z0; (1, _); (z0, 1, L, _, -)
z0; (0, _); (z0, 0, L, _, -)
z0; (&gt;, _); (q0, &gt;, R, _, -)
z1; (1, 1); (z1, 1, L, 1, -)
z1; (0, 1); (z1, 0, L, 1, -)
z1; (&gt;, 1); (q1, &gt;, R, 1, -)
z1; (1, _); (z1, 1, L, _, -)
z1; (0, _); (z1, 0, L, _, -)
z1; (&gt;, _); (q1, &gt;, R, _, -)
q0; (1,1); (q0, 1, R, 1, R)
q0; (0,1); (q0, 0, R, 1, R)
q1; (1,1); (q1, 1, R, 1, R)
q1; (0,1); (q1, 0, R, 1, R)
q1; (1, _); (N, 1, -, _, -)
q1; (0, _); (Y, 0, -, _, -)
q0; (0, _); (N, 0, -, _, -)
q0; (1, _); (Y, 1, -, _, -)
</code></pre>
<h2><a id="License_71"></a>License</h2>
<p>MIT</p>